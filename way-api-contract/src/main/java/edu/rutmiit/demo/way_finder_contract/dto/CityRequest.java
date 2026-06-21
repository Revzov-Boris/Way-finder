package edu.rutmiit.demo.way_finder_contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Schema(description = "Запрос на создание города")
public class CityRequest {
    @Schema(description = "Название населённого пункта", example = "Керчь")
    @NotBlank(message = "Название должно быть")
    @Size(min = 3, max = 100, message = "Название должно быть от 3 до 100 символов включительно")
    private String name;
    @Size(min = 5, max = 150, message = "Адрес должен быть от 5 до 150 символов включительно")
    @Schema(description = "Расположение", example = "Россия, Курская область, Обоянский район, г. Обоянь")
    private String address;
    @Schema(description = "На сколько часов опережает мировое время", example = "3")
    @DecimalMax(value = "14", message = "Максимальная зона UTC+14")
    @DecimalMin(value = "-12", message = "Минимальная зона UTC-12")
    private int timeZone;
}
