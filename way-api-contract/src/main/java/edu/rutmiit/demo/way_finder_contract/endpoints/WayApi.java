package edu.rutmiit.demo.way_finder_contract.endpoints;


import edu.rutmiit.demo.way_finder_contract.dto.WayResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Ways", description = "Поиск путей")
@RequestMapping(
        value = "/api/ways",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public interface WayApi {
    @Operation(
            summary = "Список путей",
            description = "Все пути на все даты в будущем из пункта А в пункт Б\""
    )
    @ApiResponse(responseCode = "200", description = "Список путей")
    @GetMapping
    PagedModel<EntityModel<List<WayResponse>>> getWaysByStartAndFinish(
            @Parameter(description = "Номер страницы (0..N)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @RequestParam int startCityId,
            @RequestParam int finishCityId
    );
}
