package edu.rutmiit.demo.demorest.controllers;

import edu.rutmiit.demo.demorest.assemblers.CityAssembler;
import edu.rutmiit.demo.demorest.service.CityService;
import edu.rutmiit.demo.way_finder_contract.dto.CityRequest;
import edu.rutmiit.demo.way_finder_contract.dto.CityResponse;
import edu.rutmiit.demo.way_finder_contract.endpoints.CityApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CityController implements CityApi {
    private final CityService cityService;
    private final PagedResourcesAssembler<CityResponse> pageCityAssembler;
    private final CityAssembler cityAssembler;

    @Autowired
    public CityController(CityService cityService, PagedResourcesAssembler<CityResponse> pageCityAssembler, CityAssembler cityAssembler) {
        this.cityService = cityService;
        this.pageCityAssembler = pageCityAssembler;
        this.cityAssembler = cityAssembler;
    }

    @Override
    public EntityModel<CityResponse> getCityById(Integer id) {
        return cityAssembler.toModel(cityService.findById(id));
    }

    @Override
    public PagedModel<EntityModel<CityResponse>> getAllCities(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CityResponse> paged = cityService.findAll(pageable);
        Page<CityResponse> springPage = new PageImpl<>(
                paged.getContent(),
                PageRequest.of(paged.getPageable().getPageNumber(), paged.getPageable().getPageSize()),
                paged.getTotalElements()
        );
        return pageCityAssembler.toModel(springPage, cityAssembler);
    }

    @Override
    public ResponseEntity<EntityModel<CityResponse>> createCity(CityRequest request) {
        CityResponse created = cityService.create(request);
        EntityModel<CityResponse> model = cityAssembler.toModel(created);
        return ResponseEntity
                .created(model.getRequiredLink("self").toUri())
                .body(model);
    }
}
