package edu.rutmiit.demo.way_finder_contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Getter
@Builder
public class HaltResponse extends RepresentationModel<HaltResponse> {
    private final long id;
    private final CityResponse city;
    @Schema(description = "Время (UTC+3) остановки в этом месте")
    private final LocalDateTime date;
}
