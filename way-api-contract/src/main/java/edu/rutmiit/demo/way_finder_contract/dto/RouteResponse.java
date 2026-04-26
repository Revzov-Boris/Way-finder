package edu.rutmiit.demo.way_finder_contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import java.util.List;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Schema(description = "Информация о маршруте")
public class RouteResponse extends RepresentationModel<RouteResponse> {
    @Schema(description = "ID маршрута")
    private Long id;
    @Schema(description = "Вид транспорта, на котором происходит передвижение", example = "Автобус")
    private String typeTransport;
    @Schema(description = "Тип маршрута", example = "междугородний")
    private String typeDistance;
    @Schema(description = "Остановки в порядке следования от начальной к конечной")
    private List<HaltResponse> halts;
}
