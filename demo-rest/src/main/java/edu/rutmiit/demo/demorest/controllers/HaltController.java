package edu.rutmiit.demo.demorest.controllers;

import edu.rutmiit.demo.way_finder_contract.dto.HaltRequest;
import edu.rutmiit.demo.way_finder_contract.dto.HaltResponse;
import edu.rutmiit.demo.way_finder_contract.endpoints.HaltApi;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HaltController implements HaltApi {
    @Override
    public EntityModel<HaltResponse> getHaltById(Integer id) {
        return null;
    }

    @Override
    public PagedModel<EntityModel<HaltResponse>> getAllCities(int page, int size) {
        return null;
    }

    @Override
    public ResponseEntity<EntityModel<HaltResponse>> createHalt(HaltRequest request) {
        return null;
    }
}
