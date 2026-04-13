package edu.rutmiit.demo.demorest.assemblers;

import edu.rutmiit.demo.demorest.controllers.RouteController;
import edu.rutmiit.demo.way_finder_contract.dto.RouteResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class RouteAssembler implements RepresentationModelAssembler<RouteResponse, EntityModel<RouteResponse>> {
    @Override
    public EntityModel<RouteResponse> toModel(RouteResponse route) {
        EntityModel<RouteResponse> model = EntityModel.of(route,
                linkTo(methodOn(RouteController.class).getRuoteById(route.getId())).withSelfRel()
        );
        return model;
    }
}
