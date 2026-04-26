package edu.rutmiit.demo.way_finder_contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
public class PutRouteRequest {
        @Schema(description = "Вид транспорта, на котором происходит передвижение", example = "Автобус")
        @NotBlank(message = "Тип транспорта не может быть пустым")
        @Size(min = 2, max = 50, message = "Тип должен содержать от 2 до 50 символов включительно")
        private String typeTransport;
        @NotBlank(message = "Тип маршрута не может быть пустым")
        @Size(min = 2, max = 50, message = "Тип маршрута должен содержать от 2 до 50 символов включительно")
        @Schema(description = "Тип маршрута", example = "междугородний")
        private String typeDistance;

}
