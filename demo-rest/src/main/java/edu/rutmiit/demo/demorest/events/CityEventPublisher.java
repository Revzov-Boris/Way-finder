package edu.rutmiit.demo.demorest.events;

import edu.rutmiit.demo.events.CityEvent;
import edu.rutmiit.demo.events.EventEnvelope;
import edu.rutmiit.demo.events.RoutingKeys;
import edu.rutmiit.demo.way_finder_contract.dto.CityResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 Публикация доменных событий авторов в RabbitMQ.
  */
@Component
public class CityEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(CityEventPublisher.class);
    private static final String SOURCE = "demo-rest";

    private RabbitTemplate rabbitTemplate;

    public CityEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Публикует событие «автор создан».
     */
    public void publishCreated(CityResponse city) {
        var event = new CityEvent.Created(
                city.getId(),
                city.getName(),
                city.getAddress(),
                city.getTimeZone()
        );
        send(RoutingKeys.CITY_CREATED, event);
    }


    private void send(String routingKey, CityEvent event) {
        try {
            EventEnvelope<CityEvent> envelope = EventEnvelope.wrap(event, SOURCE, routingKey);
            rabbitTemplate.convertAndSend(RoutingKeys.EXCHANGE, routingKey, envelope);
            log.info("Событие отправлено: {} [eventId={}]", routingKey, envelope.metadata().eventId());
        } catch (Exception e) {
            log.error("Не удалось отправить событие {}: {}", routingKey, e.getMessage());
        }
    }
}
