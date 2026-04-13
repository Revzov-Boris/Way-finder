package edu.rutmiit.demo.way_finder_contract.endpoints;

import edu.rutmiit.demo.way_finder_contract.config.WaysApiContractConfig;
import edu.rutmiit.demo.way_finder_contract.dto.CityRequest;
import edu.rutmiit.demo.way_finder_contract.dto.CityResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.EntityResponse;

@Tag(name = "Cities", description = "Управление городами")
@RequestMapping(
        value = "/api/cities",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public interface CityApi {
    @Operation(
            summary = "Получить город по ID",
            security = @SecurityRequirement(name = WaysApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "200", description = "Город найден")
    @GetMapping("/{id}")
    EntityModel<CityResponse> getCityById(
            @Parameter(description = "ID города", required = true, example = "1")
            @PathVariable
            Integer id
    );


    @Operation(
            summary = "Список всех городов",
            security = @SecurityRequirement(name = WaysApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "200", description = "Список городов")
    @GetMapping
    PagedModel<EntityModel<CityResponse>> getAllCities(
        @Parameter(description = "Номер страницы", example = "1") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Размер страницы", example = "1") @RequestParam(defaultValue = "10") int size
    );


    @Operation(
            summary = "Создать город",
            security = @SecurityRequirement(name = WaysApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "201", description = "Город создан")
    @ApiResponse(responseCode = "400", description = "Ошибка при создании города")
    @ApiResponse(responseCode = "409", description = "Такой город уже существует")
    @PostMapping
    ResponseEntity<EntityModel<CityResponse>> createCity(@Valid @RequestBody CityRequest request);
}
