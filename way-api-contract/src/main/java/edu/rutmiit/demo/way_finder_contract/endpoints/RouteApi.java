package edu.rutmiit.demo.way_finder_contract.endpoints;

import edu.rutmiit.demo.way_finder_contract.config.WaysApiContractConfig;
import edu.rutmiit.demo.way_finder_contract.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Routes", description = "Управление маршрутами")
@RequestMapping(
        value = "/api/routs",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public interface RouteApi {
    @Operation(
            summary = "Получить маршрут по ID",
            security = @SecurityRequirement(name = WaysApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @GetMapping("/{id}")
    EntityModel<RouteResponse> getRuoteById(
        @Parameter(description = "ID маршрута", required = true, example = "1")
        @PathVariable
        Long id
    );


    @Operation(
            summary = "Список маршрутов",
            security = @SecurityRequirement(name = WaysApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "200", description = "Список маршрутов")
    @GetMapping
    PagedModel<EntityModel<RouteResponse>> getAllRouts(
        @Parameter(description = "Номер страницы", example = "1") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Размер страницы", example = "1") @RequestParam(defaultValue = "10") int size

    );


    @Operation(
            summary = "Создать маршрут",
            security = @SecurityRequirement(name = WaysApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "201", description = "Маршрут создана. Location header содержит URI нового ресурса.")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = Exception.class)))
    @ApiResponse(responseCode = "409", description = "Такой маршрут уже создан",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<EntityModel<RouteResponse>> createRoute(@Valid @RequestBody RouteRequest request);


    @Operation(
            summary = "Частичное обновление маршрута (PATCH)",
            description = """
                    Обновляет только переданные поля.
                    """,
            security = @SecurityRequirement(name = WaysApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "200", description = "Обновлён маршрут")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Маршрут не найден",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    EntityModel<RouteResponse> patchRoute(
            @Parameter(description = "ID маршрута", required = true, example = "1") @PathVariable Long id,
            @Valid @RequestBody PatchRouteRequest request
    );


    @Operation(
            summary = "Полное обновление PUT",
            description = """
                    Обновляет только переданные поля.
                    Непереданные поля остаются без изменений.
                    """,
            security = @SecurityRequirement(name = WaysApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "200", description = "Обновлён маршрут")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Маршрут не найден",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    EntityModel<RouteResponse> putRoute(
            @Parameter(description = "ID маршрута", required = true, example = "1") @PathVariable Long id,
            @Valid @RequestBody PutRouteRequest request
    );



    @Operation(
            summary = "Удалить маршрут",
            security = @SecurityRequirement(name = WaysApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "204", description = "Маршрут удален")
    @ApiResponse(responseCode = "404", description = "Маршрут не найдена",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    EntityModel<RouteResponse> deleteRoute(
            @Parameter(description = "ID маршрута", required = true, example = "1") @PathVariable Long id
    );
}
