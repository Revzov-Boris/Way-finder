package edu.rutmiit.demo.way_finder_contract.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Relation(collectionRelation = "ways", itemRelation = "way")
@Schema(description = "Информация о пути")
public class WayResponse extends RepresentationModel<WayResponse> {
    private final List<RouteResponse> routes;
}
