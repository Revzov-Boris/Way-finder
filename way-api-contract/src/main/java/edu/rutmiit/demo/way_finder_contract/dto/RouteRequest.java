package edu.rutmiit.demo.way_finder_contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Запрос на создание маршрута")
public class RouteRequest {
    @NotNull
    @NotEmpty
    @Schema(description = "ID остановок в порядке следования от начальной к конечной")
    List<Long> haltIds;
    @Schema(description = "Вид транспорта, на котором происходит передвижение", example = "Автобус")
    @NotBlank(message = "Тип не может быть пустым")
    @Size(min = 2, max = 50, message = "Тип должен содержать от 2 до 50 символов включительно")
    private String type;
}
