package edu.rutmiit.demo.events;

public sealed interface RouteEvent {
    record Created (
            Long id,
            String typeTransport,
            String typeDistance
    ) implements RouteEvent {}

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
