package edu.rutmiit.demo.demorest.events;

import edu.rutmiit.demo.events.EventEnvelope;
import edu.rutmiit.demo.events.RouteEvent;
import edu.rutmiit.demo.events.RoutingKeys;
import edu.rutmiit.demo.way_finder_contract.dto.RouteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RouteEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(CityEventPublisher.class);
    private static final String SOURCE = "demo-rest";

    private RabbitTemplate rabbitTemplate;

    public RouteEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    public void publishCreated(RouteResponse route) {
        var event = new RouteEvent.Created(
                route.getId(),
                route.getTypeTransport(),
                route.getTypeDistance()
        );
        send(RoutingKeys.ROUTE_CREATED, event);
    }

    public void publishPatchupdated(RouteResponse route) {
        var event = new RouteEvent.PatchUpdated(
                route.getId(),
                route.getTypeTransport(),
                route.getTypeDistance()
        );
        send(RoutingKeys.ROUTE_PATCHUPDATED, event);
    }


    public void publishUpdated(RouteResponse route) {
        var event = new RouteEvent.Updated(
                route.getId(),
                route.getTypeTransport(),
                route.getTypeDistance()
        );
        send(RoutingKeys.ROUTE_UPDATED, event);
    }


    public void publishDeleted(RouteResponse route) {
        var event = new RouteEvent.Deleted(
                route.getId(),
                route.getTypeTransport(),
                route.getTypeDistance()
        );
        send(RoutingKeys.ROUTE_DELETED, event);
    }


    private void send(String routingKey, RouteEvent event) {
        try {
            EventEnvelope<RouteEvent> envelope = EventEnvelope.wrap(event, SOURCE, routingKey);
            rabbitTemplate.convertAndSend(RoutingKeys.EXCHANGE, routingKey, envelope);
            log.info("Событие отправлено: {} [eventId={}]", routingKey, envelope.metadata().eventId());
        } catch (Exception e) {
            log.error("Не удалось отправить событие {}: {}", routingKey, e.getMessage());
        }
    }
}
