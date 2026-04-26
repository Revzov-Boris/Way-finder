package edu.rutmiit.demo.demorest.assemblers;

import edu.rutmiit.demo.demorest.controllers.CityController;
import edu.rutmiit.demo.demorest.controllers.HaltController;
import edu.rutmiit.demo.way_finder_contract.dto.CityResponse;
import edu.rutmiit.demo.way_finder_contract.dto.HaltResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class HaltAssembler implements RepresentationModelAssembler<HaltResponse, EntityModel<HaltResponse>> {
    @Override
    public EntityModel<HaltResponse> toModel(HaltResponse halt) {
        EntityModel<HaltResponse> model = EntityModel.of(halt,
                linkTo(methodOn(HaltController.class).getHaltById(halt.getId())).withSelfRel()
        );
        return model;
    }
}
