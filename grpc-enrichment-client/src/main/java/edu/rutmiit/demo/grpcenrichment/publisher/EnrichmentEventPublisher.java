package edu.rutmiit.demo.grpcenrichment.publisher;

import edu.rutmiit.demo.events.RouteEvent;
import edu.rutmiit.demo.events.EventEnvelope;
import edu.rutmiit.demo.events.RoutingKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Публикация событий обогащения (book.enriched) в RabbitMQ.
 *
 * Аналогичен RouteEventPublisher в demo-rest, но публикует другой тип события.
 * Паттерн fire-and-forget: если RabbitMQ недоступен, ошибка логируется,
 * но gRPC-вызов уже выполнен — результат не теряется полностью.
 */
@Component
public class EnrichmentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(EnrichmentEventPublisher.class);
    private static final String SOURCE = "grpc-enrichment-client";

    private final RabbitTemplate rabbitTemplate;

    public EnrichmentEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Публикует событие book.enriched с результатами gRPC-аналитики.
     */
    public void publishEnriched(RouteEvent.Enriched enrichedEvent) {
        try {
            EventEnvelope<RouteEvent> envelope = EventEnvelope.wrap(
                    enrichedEvent, SOURCE, RoutingKeys.ROUTE_ENRICHED);

            rabbitTemplate.convertAndSend(
                    RoutingKeys.EXCHANGE,
                    RoutingKeys.ROUTE_ENRICHED,
                    envelope);

            log.info("Событие отправлено: {} [routeId={}, eventId={}]",
                    RoutingKeys.ROUTE_ENRICHED,
                    enrichedEvent.id(),
                    envelope.metadata().eventId());

        } catch (Exception e) {
            log.error("Не удалось отправить событие {}: {}",
                    RoutingKeys.ROUTE_ENRICHED, e.getMessage());
        }
    }
}
