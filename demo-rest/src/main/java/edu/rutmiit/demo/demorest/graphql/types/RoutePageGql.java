package edu.rutmiit.demo.demorest.graphql.types;

import edu.rutmiit.demo.way_finder_contract.dto.RouteResponse;

import java.util.List;

public record RoutePageGql(
        List<RouteResponse> content,
        PageInfoGql pageInfo,
        int totalElements
){}

