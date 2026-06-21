package edu.rutmiit.demo.way_finder_contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Запрос на создание маршрута")
public class RouteRequest {
    @Schema(description = "Вид транспорта, на котором происходит передвижение", example = "Автобус")
    @NotBlank(message = "Тип транспорта не может быть пустым")
    @Size(min = 2, max = 50, message = "Тип должен содержать от 2 до 50 символов включительно")
    private String typeTransport;
    @NotBlank(message = "Тип маршрута не может быть пустым")
    @Size(min = 2, max = 50, message = "Тип маршрута должен содержать от 2 до 50 символов включительно")
    @Schema(description = "Тип маршрута", example = "междугородний")
    private String typeDistance;
    @Valid
    @Schema(description = "Остановки этого маршрута")
    private List<HaltInRoute> halts;


    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    @Schema(description = "Запрос на создание остановки при создании маршрута, не требует ID маршрута, так он ещё не создан")
    public static class HaltInRoute {
        @NotNull(message = "ID города должен быть")
        @Schema(description = "ID города, в котором находится остановка", example = "1")
        private Integer cityId;
        @NotNull(message = "Временная зона должна быть")
        @Schema(description = "Время (UTC+3) остановки в этом месте", example = "2026-06-01T20:45:00")
        private LocalDateTime date;
    }
}
