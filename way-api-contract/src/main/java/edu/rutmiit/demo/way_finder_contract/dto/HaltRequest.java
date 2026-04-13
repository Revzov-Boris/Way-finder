package edu.rutmiit.demo.way_finder_contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public class HaltRequest {
    @Schema(description = "ID города, в котором находится остановка")
    private Integer cityId;
    @Schema(description = "Время (UTC+3) остановки в этом месте")
    private LocalDateTime date;
}
