package edu.rutmiit.demo.grpcenrichment.listener;

import edu.rutmiit.demo.grpc.HaltInfo;
import org.springframework.stereotype.Component;
import edu.rutmiit.demo.events.RouteEvent;
import edu.rutmiit.demo.events.EventMetadata;
import edu.rutmiit.demo.grpc.AnalyzeRouteRequest;
import edu.rutmiit.demo.grpc.RouteAnalysisResponse;
import edu.rutmiit.demo.grpc.RouteAnalyticsGrpc;
import edu.rutmiit.demo.grpcenrichment.publisher.EnrichmentEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Component
public class RouteCreatedListener {
    private static final Logger log = LoggerFactory.getLogger(RouteCreatedListener.class);

    private final RouteAnalyticsGrpc.RouteAnalyticsBlockingStub analyticsStub;
    private final EnrichmentEventPublisher enrichmentPublisher;
    private final JsonMapper jsonMapper;

    public RouteCreatedListener(RouteAnalyticsGrpc.RouteAnalyticsBlockingStub analyticsStub,
                               EnrichmentEventPublisher enrichmentPublisher,
                               JsonMapper jsonMapper) {
        this.analyticsStub = analyticsStub;
        this.enrichmentPublisher = enrichmentPublisher;
        this.jsonMapper = jsonMapper;
    }

    /**
     * Обрабатывает событие route.created:
     * 1. Десериализует событие из JSON
     * 2. Формирует gRPC-запрос
     * 3. Вызывает gRPC-сервер (синхронно)
     * 4. Публикует результат как событие route.enriched
     */
    @RabbitListener(queues = "q.enrichment.route-created", messageConverter = "")
    public void handleRouteCreated(Message message) {
        try {
            // 1. Парсим JSON-конверт
            byte[] body = message.getBody();
            JsonNode root = jsonMapper.readTree(body);

            JsonNode metaNode = root.get("metadata");
            EventMetadata metadata = jsonMapper.treeToValue(metaNode, EventMetadata.class);

            JsonNode payloadNode = root.get("payload");
            RouteEvent.Created routeCreated = jsonMapper.treeToValue(payloadNode, RouteEvent.Created.class);

            log.info("Получено событие route.created: id={}, «type={}» [eventId={}]",
                    routeCreated.id(), routeCreated.typeTransport(), metadata.eventId());

            // 2. Формируем gRPC-запрос
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            List<HaltInfo> halts = new ArrayList<>();
            if (routeCreated.haltInfos() != null) {
                for (RouteEvent.HaltInfo h : routeCreated.haltInfos()) {
                    String strDate = h.date().format(dateFormat);
                    HaltInfo halt = HaltInfo.newBuilder()
                            .setDate(strDate)
                            .setHaltId(h.id())
                            .setCityId(h.cityId())
                            .build();
                    halts.add(halt);
                }
            }
            AnalyzeRouteRequest grpcRequest = AnalyzeRouteRequest.newBuilder()
                    .addAllHalts(halts)
                    .setRouteId(routeCreated.id())
                    .setTypeTransport(routeCreated.typeTransport())
                    .setTypeDistance(routeCreated.typeDistance())
                    .build();

            // 3. Вызываем gRPC-сервер (синхронно)
            log.info("Вызов gRPC: RouteAnalytics.AnalyzeRoute(id={})", routeCreated.id());
            RouteAnalysisResponse grpcResponse = analyticsStub.analyzeRoute(grpcRequest);

            log.info("gRPC ответ получен: routeId={}, время в пути = {}мин, шанс отмены рейса = {}, сложность={}/10",
                    grpcResponse.getRouteId(),
                    grpcResponse.getTimeInWay(),
                    grpcResponse.getChanceOfCancellation(),
                    grpcResponse.getDifficultyLevel()
            );


            // 4. Публикуем событие route.enriched
            RouteEvent.Enriched enrichedEvent = new RouteEvent.Enriched(
                    grpcResponse.getRouteId(),
                    grpcResponse.getTimeInWay(),
                    grpcResponse.getChanceOfCancellation(),
                    grpcResponse.getDifficultyLevel()
            );

            enrichmentPublisher.publishEnriched(enrichedEvent);

            log.info("Маршрут обогащён: id={}, «{}» и route.enriched отправлено",
                    routeCreated.id(), routeCreated.typeTransport());

        } catch (io.grpc.StatusRuntimeException e) {
            log.error("gRPC ошибка при обогащении маршрута: {} ({})",
                    e.getStatus().getDescription(), e.getStatus().getCode());
            throw new RuntimeException("gRPC-вызов завершился ошибкой", e);

        } catch (Exception e) {
            log.error("Ошибка обработки события route.created: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось обработать событие route.created", e);
        }
    }
}
