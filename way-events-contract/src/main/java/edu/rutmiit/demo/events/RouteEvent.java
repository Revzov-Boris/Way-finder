package edu.rutmiit.demo.events;

import java.time.LocalDateTime;
import java.util.List;

public sealed interface RouteEvent {
    record Created (
            Long id,
            String typeTransport,
            String typeDistance,
            List<HaltInfo> haltInfos
    ) implements RouteEvent {}

    record HaltInfo(
            Long id,
            Long routeId,
            Integer cityId,
            LocalDateTime date
    ) {}

    record Updated (
            Long id,
            String typeTransport,
            String typeDistance
    ) implements RouteEvent {}

    record PatchUpdated (
            Long id,
            String typeTransport,
            String typeDistance
    ) implements RouteEvent {}

    record Deleted (
            Long id,
            String typeTransport,
            String typeDistance
    ) implements RouteEvent {}

    record Enriched(
            Long id,
            Integer timeInWay,
            Double chanceOfCancellation,
            Integer difficultyLevel
    ) implements RouteEvent {}
}
