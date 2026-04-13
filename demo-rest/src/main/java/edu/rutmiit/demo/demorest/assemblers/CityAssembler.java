package edu.rutmiit.demo.demorest.assemblers;

import edu.rutmiit.demo.demorest.controllers.CityController;
import edu.rutmiit.demo.way_finder_contract.dto.CityResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CityAssembler implements RepresentationModelAssembler<CityResponse, EntityModel<CityResponse>> {
    @Override
    public EntityModel<CityResponse> toModel(CityResponse city) {
        EntityModel<CityResponse> model = EntityModel.of(city,
                linkTo(methodOn(CityController.class).getCityById(city.getId())).withSelfRel()
        );
        return model;
    }
}
