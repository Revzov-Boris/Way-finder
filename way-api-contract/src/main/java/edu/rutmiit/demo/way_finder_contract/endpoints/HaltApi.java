package edu.rutmiit.demo.way_finder_contract.endpoints;

import edu.rutmiit.demo.way_finder_contract.config.WaysApiContractConfig;
import edu.rutmiit.demo.way_finder_contract.dto.HaltRequest;
import edu.rutmiit.demo.way_finder_contract.dto.HaltResponse;
import edu.rutmiit.demo.way_finder_contract.dto.PatchHaltRequest;
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

@Tag(name = "Halts", description = "Управление остановками")
@RequestMapping(
        value = "/api/halts",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public interface HaltApi {
    @Operation(
            summary = "Получить остановку по ID",
            security = @SecurityRequirement(name = WaysApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "200", description = "Остановка найден")
    @GetMapping("/{id}")
    EntityModel<HaltResponse> getHaltById(
            @Parameter(description = "ID остановки", required = true, example = "1")
            @PathVariable
            Long id
    );


    @Operation(
            summary = "Список всех остановок",
            security = @SecurityRequirement(name = WaysApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "200", description = "Список остановок")
    @GetMapping
    PagedModel<EntityModel<HaltResponse>> getAllHalts(
        @Parameter(description = "Номер страницы", example = "1") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Размер страницы", example = "1") @RequestParam(defaultValue = "10") int size
    );


    @Operation(
            summary = "Создать остановку",
            security = @SecurityRequirement(name = WaysApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "201", description = "Остановка создана")
    @ApiResponse(responseCode = "400", description = "Ошибка при создании остановки")
    @ApiResponse(responseCode = "409", description = "Такая остановка уже существует")
    @PostMapping
    ResponseEntity<EntityModel<HaltResponse>> createHalt(@Valid @RequestBody HaltRequest request);


    @Operation(
            summary = "Обновить остановку",
            security = @SecurityRequirement(name = WaysApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "200", description = "Остановка обновлена")
    @ApiResponse(responseCode = "400", description = "Ошибка при обновлении остановки")
    @PatchMapping("/{id}")
    EntityModel<HaltResponse> patchHalt(@Valid @RequestBody PatchHaltRequest request,
                                        @Parameter(description = "ID остановки", required = true, example = "1")
                                        @PathVariable long id);

    @Operation(
            summary = "Удалить остановку",
            security = @SecurityRequirement(name = WaysApiContractConfig.SECURITY_SCHEME_BEARER),
            description = "Удаляет остановку"
    )
    @ApiResponse(responseCode = "200", description = "Остановка удалена")
    @ApiResponse(responseCode = "400", description = "Ошибка при удалении остановки")
    @DeleteMapping("/{id}")
    EntityModel<HaltResponse> deleteHalt(@Parameter(description = "ID остановки", required = true, example = "1")
                                         @PathVariable long id);

}
