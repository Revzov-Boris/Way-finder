package edu.rutmiit.demo.way_finder_contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Getter
@Builder
public class HaltResponse extends RepresentationModel<HaltResponse> {
    @Schema(description = "ID остановки")
    private final long id;
    @Schema(description = "ID пункта, в котором происходит остановка")
    private final Integer cityId;
    @Schema(description = "ID маршрута")
    private final Long routeId;
    @Schema(description = "Время (UTC+3) остановки в этом месте")
    private final LocalDateTime date;
}
