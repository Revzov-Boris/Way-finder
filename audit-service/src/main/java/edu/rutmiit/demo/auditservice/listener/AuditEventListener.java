package edu.rutmiit.demo.auditservice.listener;

import edu.rutmiit.demo.auditservice.models.AuditEntry;
import edu.rutmiit.demo.auditservice.storage.AuditStorage;
import edu.rutmiit.demo.events.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import java.time.Instant;

@Component
public class AuditEventListener {
    private static final Logger log = LoggerFactory.getLogger(AuditEventListener.class);

    private final AuditStorage auditStorage;
    private final JsonMapper jsonMapper;

    public AuditEventListener(AuditStorage auditStorage, JsonMapper jsonMapper) {
        this.auditStorage = auditStorage;
        this.jsonMapper = jsonMapper;
    }


    /**
     * Принимает все события из очереди q.audit.events.
     *
     * Десериализация выполняется в два этапа:
     * 1. Парсим JSON в дерево узлов (JsonNode) — быстро и безопасно.
     * 2. Извлекаем metadata и определяем тип payload по полю eventType.
     * 3. Десериализуем payload в конкретный record по выявленному типу.
     */
    @RabbitListener(queues = "q.audit.events", messageConverter = "")
    public void handleEvent(Message message) {
        try {
            byte[] body = message.getBody();
            JsonNode root = jsonMapper.readTree(body);

            // Извлекаем метаданные из JSON-конверта
            JsonNode metaNode = root.get("metadata");
            EventMetadata metadata = jsonMapper.treeToValue(metaNode, EventMetadata.class);

            // Дедупликация — если событие уже обработано, пропускаем
            if (auditStorage.isDuplicate(metadata.eventId())) {
                log.warn("Дубликат события пропущен: eventId={}", metadata.eventId());
                return;
            }

            // Определяем тип события и формируем описание
            JsonNode payloadNode = root.get("payload");
            String description = buildDescription(metadata.eventType(), payloadNode);

            AuditEntry entry = auditStorage.save(new AuditEntry(
                    0,
                    metadata.eventId(),
                    metadata.eventType(),
                    metadata.source(),
                    metadata.timestamp(),
                    Instant.now(),
                    description
            ));

            log.info("[AUDIT #{}] {} | {}", entry.sequenceNumber(), metadata.eventType(), description);

        } catch (Exception e) {
            log.error("Ошибка обработки события: {}", e.getMessage(), e);
            // Исключение пробросится, сообщение уйдёт в DLQ после исчерпания retries
            throw new RuntimeException("Не удалось обработать событие", e);
        }
    }


    /**
     * Формирует человекочитаемое описание события для аудит-лога.
     *
     * Десериализует payload в конкретный тип на основе eventType,
     * затем формирует описание через pattern matching по sealed interface.
     */
    private String buildDescription(String eventType, JsonNode payloadNode) throws Exception {
        return switch (eventType) {
            case "city.created" -> {
                CityEvent.Created e = jsonMapper.treeToValue(payloadNode, CityEvent.Created.class);
                yield String.format("Создан город «%s» (ID: %s), адрес: %s",
                        e.name(), e.id(), e.address());
            }
            case RoutingKeys.CITY_PATCHUPDATED -> {
                CityEvent.Patchupdated e = jsonMapper.treeToValue(payloadNode, CityEvent.Patchupdated.class);
                yield String.format("Частично обновлён город «%s» (ID: %s), адрес: %s",
                        e.name(), e.id(), e.address());
            }
            case RoutingKeys.CITY_DELETED -> {
                CityEvent.Deleted e = jsonMapper.treeToValue(payloadNode, CityEvent.Deleted.class);
                yield String.format("Удалён город «%s» (ID: %s), адрес: %s",
                        e.name(), e.id(), e.address());
            }
            case "halt.created" -> {
                HaltEvent.Created e = jsonMapper.treeToValue(payloadNode, HaltEvent.Created.class);
                yield String.format("Создана остановка с ID=%s (на маршруте %s на время %s)",
                        e.id(), e.routeId(), e.date());
            }
            case "halt.patchupdated" -> {
                HaltEvent.PatchUpdated e = jsonMapper.treeToValue(payloadNode, HaltEvent.PatchUpdated.class);
                yield String.format("Обновлёна остановка ID=%s",
                        e.id());
            }
            case RoutingKeys.HALT_DELETED -> {
                HaltEvent.Deleted e = jsonMapper.treeToValue(payloadNode, HaltEvent.Deleted.class);
                yield String.format("Удалена остановка ID=%s",
                        e.id());
            }
            case "route.created" -> {
                RouteEvent.Created e = jsonMapper.treeToValue(payloadNode, RouteEvent.Created.class);
                yield String.format("Создан маршрут типа %s (ID=%d)",
                        e.typeTransport(), e.id());
            }
            case "route.updated" -> {
                RouteEvent.Created e = jsonMapper.treeToValue(payloadNode, RouteEvent.Created.class);
                yield String.format("Обновлён маршрут (ID=%d)", e.id());
            }
            case "route.patchupdated" -> {
                RouteEvent.PatchUpdated e = jsonMapper.treeToValue(payloadNode, RouteEvent.PatchUpdated.class);
                yield String.format("Частично обновлён маршрут ID=%d", e.id());
            }
            case "route.deleted" -> {
                RouteEvent.Deleted e = jsonMapper.treeToValue(payloadNode, RouteEvent.Deleted.class);
                yield String.format("Удалён маршрут ID=%d", e.id());
            }
            case "route.enriched" -> {
                RouteEvent.Enriched e = jsonMapper.treeToValue(payloadNode, RouteEvent.Enriched.class);
                yield String.format("Проанализирован маршрут ID=%d: время в пути = %s мин, шанс отмены рейса = %s, сложность=%s/10",
                        e.id(), e.timeInWay(), e.chanceOfCancellation(), e.difficultyLevel());
            }
            default -> "Неизвестное событие: " + eventType;
        };
    }

}
