package edu.rutmiit.demo.way_finder_contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class HaltRequest {
    @Schema(description = "ID города, в котором находится остановка", example = "1")
    private Integer cityId;
    @Schema(description = "ID маршрута", example = "1")
    private Long routeId;
    @Schema(description = "Время (UTC+3) остановки в этом месте")
    private LocalDateTime date;
}
