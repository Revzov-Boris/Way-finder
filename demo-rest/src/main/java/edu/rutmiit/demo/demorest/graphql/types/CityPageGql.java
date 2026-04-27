package edu.rutmiit.demo.demorest.graphql.types;

import edu.rutmiit.demo.way_finder_contract.dto.CityResponse;

import java.util.List;

public record CityPageGql(
    List<CityResponse> content,
    PageInfoGql pageInfo,
    int totalElements
){}
