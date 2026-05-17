package edu.rutmiit.demo.demorest.graphql.types;

import edu.rutmiit.demo.way_finder_contract.dto.HaltResponse;
import java.util.List;

public record HaltPageGql(
        List<HaltResponse> content,
        PageInfoGql pageInfo,
        int totalElements
){}

