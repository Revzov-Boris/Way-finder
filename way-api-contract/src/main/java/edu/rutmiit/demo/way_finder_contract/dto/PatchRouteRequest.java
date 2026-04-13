package edu.rutmiit.demo.way_finder_contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public class PatchRouteRequest {
        Long id;
        @Schema(description = "Остановки в порядке следования от начальной к конечной")
        List<HaltRequest> halts;
}
