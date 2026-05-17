package edu.rutmiit.demo.events;

import java.time.LocalDateTime;

public sealed interface HaltEvent {
    record Created(
            Long id,
            Integer cityId,
            Long routeId,
            LocalDateTime date
    ) implements HaltEvent {}

    record Updated (
            Long id,
            Integer cityId,
            Long routeId,
            LocalDateTime date
    ) implements HaltEvent {}
}
