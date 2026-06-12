package edu.rutmiit.demo.events;

import java.time.LocalDateTime;

public sealed interface HaltEvent {
    record Created(
            Long id,
            Integer cityId,
            Long routeId,
            LocalDateTime date
    ) implements HaltEvent {}

    record PatchUpdated(
            Long id,
            Integer cityId,
            Long routeId,
            LocalDateTime date
    ) implements HaltEvent {}
}
