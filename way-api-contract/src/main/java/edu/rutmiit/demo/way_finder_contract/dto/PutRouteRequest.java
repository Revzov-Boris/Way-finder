package edu.rutmiit.demo.way_finder_contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class PutRouteRequest {
        @NotBlank
        Long id;
        @NotNull
        @Schema(description = "Остановки в порядке следования от начальной к конечной")
        List<HaltRequest> halts;

}
