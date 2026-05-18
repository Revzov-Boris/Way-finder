package edu.rutmiit.demo.events;

public sealed interface CityEvent {
    record Created(
            Integer id,
            String name,
            String address,
            Integer timeZone
    ) implements CityEvent{}
}
