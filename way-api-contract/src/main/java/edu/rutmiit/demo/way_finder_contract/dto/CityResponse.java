package edu.rutmiit.demo.way_finder_contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Builder
@Schema(description = "Информация о пункте, где есть остановки транспорта")
public class CityResponse extends RepresentationModel<CityResponse> {
    private final Integer id;
    @Schema(description = "Название города")
    private final String name;
    @Schema(description = "Расположение", example = "Россия, Курская область, Обоянский район, г. Обоянь")
    private final String address;
    @Schema(description = "На сколько часов опережает мировое время", example = "3")
    private final int timeZone;
}
