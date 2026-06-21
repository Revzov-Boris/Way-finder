package edu.rutmiit.demo.way_finder_contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Запрос на создание остановки")
public class HaltRequest {
    @NotNull(message = "ID города должен быть")
    @Schema(description = "ID города, в котором находится остановка", example = "1")
    private Integer cityId;
    @NotNull(message = "ID маршрута должен быть")
    @Schema(description = "ID маршрута", example = "1")
    private Long routeId;
    @NotNull(message = "Временная зона должна быть")
    @Schema(description = "Время (UTC+3) остановки в этом месте", example = "2026-06-01T20:45:00")
    private LocalDateTime date;
}
