package edu.rutmiit.demo.notificationservice.listener;

import edu.rutmiit.demo.events.*;
import edu.rutmiit.demo.notificationservice.websocket.NotificationWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Слушатель всех доменных событий из RabbitMQ.
 *
 * Получает события из очереди q.notifications.all (binding "#"),
 * формирует человекочитаемое JSON-уведомление и рассылает
 * всем подключённым WebSocket-клиентам через NotificationWebSocketHandler.
 *
 * Дедупликация — по eventId (на случай повторной доставки RabbitMQ).
 */
@Component
public class EventNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(EventNotificationListener.class);

    private final NotificationWebSocketHandler webSocketHandler;
    private final JsonMapper jsonMapper;

    /** Набор обработанных eventId для дедупликации. */
    private final Set<String> processedEventIds = ConcurrentHashMap.newKeySet();

    public EventNotificationListener(NotificationWebSocketHandler webSocketHandler,
                                     JsonMapper jsonMapper) {
        this.webSocketHandler = webSocketHandler;
        this.jsonMapper = jsonMapper;
    }

    @RabbitListener(queues = "q.notifications.all", messageConverter = "")
    public void handleEvent(Message message) {
        try {
            byte[] body = message.getBody();
            JsonNode root = jsonMapper.readTree(body);

            // Парсим метаданные
            JsonNode metaNode = root.get("metadata");
            EventMetadata metadata = jsonMapper.treeToValue(metaNode, EventMetadata.class);

            // Дедупликация по eventId 
            if (!processedEventIds.add(metadata.eventId())) {
                log.warn("Дубликат уведомления пропущен: eventId={}", metadata.eventId());
                return;
            }

            // Формируем уведомление
            JsonNode payloadNode = root.get("payload");
            String title = buildTitle(metadata.eventType());
            String description = buildDescription(metadata.eventType(), payloadNode);
            String icon = resolveIcon(metadata.eventType());
            String level = resolveLevel(metadata.eventType());

            // JSON для WebSocket-клиента 
            String notificationJson = jsonMapper.writeValueAsString(
                    new NotificationPayload(
                            "NOTIFICATION",
                            metadata.eventId(),
                            metadata.eventType(),
                            title,
                            description,
                            icon,
                            level,
                            metadata.source(),
                            metadata.timestamp().toString(),
                            Instant.now().toString()
                    )
            );

            // Broadcast в WebSocket
            webSocketHandler.broadcast(notificationJson);

            log.info("[NOTIFY] {} | {} (клиентов: {})",
                    metadata.eventType(), description, webSocketHandler.getActiveConnectionCount());

        } catch (Exception e) {
            log.error("Ошибка обработки события для уведомлений: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось обработать событие", e);
        }
    }

    // Формирование заголовка уведомления

    private String buildTitle(String eventType) {
        return switch (eventType) {
            case "city.created"             -> "Новый город";
            case "city.patchupdated"        -> "Город частично обновлён";
            case "city.deleted"             -> "Город удалён";
            case "halt.created"             -> "Новая остановка";
            case "halt.patchupdated"        -> "Остановка частично обновлена";
            case RoutingKeys.HALT_DELETED   -> "Остановка удалена";
            case "route.created"            -> "Новый маршрут";
            case "route.patchupdated"       -> "Маршрут частично обновлён";
            case "route.deleted"            -> "Маршрут удалён";
            case "route.updated"            -> "Маршрут обновлён";
            case "route.enriched"           -> "Маршрут проанализирован";
            default                     -> "Событие: " + eventType;
        };
    }

    // Формирование описания

    private String buildDescription(String eventType, JsonNode payloadNode) {
        try {
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
                    yield String.format("Удалён обновлён город «%s» (ID: %s), адрес: %s",
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
                    yield String.format("Маршрут id=%s проанализирован: время в пути = %s минут, шанс отмены рейса = %s, сложность = %s/10",
                            e.id(), e.timeInWay(), e.chanceOfCancellation(), e.difficultyLevel());
                }
                default -> "Неизвестное событие: " + eventType;
            };
        } catch (Exception e) {
            return "Событие " + eventType + " (ошибка парсинга)";
        }
    }

    // Иконка по типу события

    private String resolveIcon(String eventType) {
        return switch (eventType) {
            case "route.created"                -> "route-plus";
            case "route.updated"                -> "route-edit";
            case "route.patchupdated"           -> "route-patchedit";
            case "route.deleted"                -> "route-remove";
            case "route.enriched"               -> "analytics";
            case "city.created"                 -> "city-plus";
            case "city.patchupdated"            -> "city-patchedit";
            case "city.deleted"                 -> "city-remove";
            case "halt.created"                 -> "halt-plus";
            case "halt.patchupdated"            -> "halt-edit";
            case RoutingKeys.HALT_DELETED       -> "halt-remove";
            default                             -> "bell";
        };
    }

    // Уровень уведомления

    private String resolveLevel(String eventType) {
        return switch (eventType) {
            case "route.deleted", "city.deleted"        -> "warning";
            case "route.enriched"                       -> "info";
            default                                     -> "success";
        };
    }

    /**
     * Payload уведомления для WebSocket.
     */
    record NotificationPayload(
            String type,
            String eventId,
            String eventType,
            String title,
            String description,
            String icon,
            String level,
            String source,
            String eventTimestamp,
            String receivedAt
    ) {}
}
