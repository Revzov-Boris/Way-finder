package edu.rutmiit.demo.way_finder_contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Getter
@Builder
@Schema(description = "Информация о маршруте")
public class RouteResponse extends RepresentationModel<RouteResponse> {
    @Schema(description = "ID маршрута")
    private final Long id;
    @Schema(description = "Вид транспорта, на котором происходит передвижение", example = "Автобус")
    private String type;
    @Schema(description = "Остановки в порядке следования от начальной к конечной")
    private final List<HaltResponse> halts;
}
