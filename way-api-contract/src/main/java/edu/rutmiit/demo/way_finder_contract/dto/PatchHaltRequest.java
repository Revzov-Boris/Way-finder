package edu.rutmiit.demo.way_finder_contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class PatchHaltRequest {
    @Schema(description = "ID города, в котором находится остановка", example = "1")
    private Integer cityId;
    @Schema(description = "ID маршрута", example = "1")
    private Long routeId;
    @Schema(description = "Время (UTC+3) остановки в этом месте", example = "2026-06-01T20:45:00")
    private LocalDateTime date;
}
