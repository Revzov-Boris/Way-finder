package edu.rutmiit.demo.demorest.graphql.types;

import java.time.LocalDateTime;
import java.util.List;

public record CreateRouteGql(
        String typeTransport,
        String typeDistance,
        List<HaltIntRouteHql> halts) {

    public static record HaltIntRouteHql(
            int cityId,
            LocalDateTime date
    ) {}
}
