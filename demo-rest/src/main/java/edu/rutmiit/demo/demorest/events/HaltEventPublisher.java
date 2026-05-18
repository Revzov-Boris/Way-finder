package edu.rutmiit.demo.demorest.events;

import edu.rutmiit.demo.events.EventEnvelope;
import edu.rutmiit.demo.events.HaltEvent;
import edu.rutmiit.demo.events.RoutingKeys;
import edu.rutmiit.demo.way_finder_contract.dto.HaltResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class HaltEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(HaltEventPublisher.class);
    private static final String SOURCE = "demo-rest";

    private RabbitTemplate rabbitTemplate;

    public HaltEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    public void publishCreated(HaltResponse halt) {
        var event = new HaltEvent.Created(
                halt.getId(),
                halt.getCityId(),
                halt.getRouteId(),
                halt.getDate()
        );
        send(RoutingKeys.HALT_CREATED, event);
    }

    public void publishPatchupdated(HaltResponse halt) {
        var event = new HaltEvent.Updated(
                halt.getId(),
                halt.getCityId(),
                halt.getRouteId(),
                halt.getDate()
        );
        send(RoutingKeys.HALT_CREATED, event);
    }


    private void send(String routingKey, HaltEvent event) {
        try {
            EventEnvelope<HaltEvent> envelope = EventEnvelope.wrap(event, SOURCE, routingKey);
            rabbitTemplate.convertAndSend(RoutingKeys.EXCHANGE, routingKey, envelope);
            log.info("Событие отправлено: {} [eventId={}]", routingKey, envelope.metadata().eventId());
        } catch (Exception e) {
            log.error("Не удалось отправить событие {}: {}", routingKey, e.getMessage());
        }
    }
}
