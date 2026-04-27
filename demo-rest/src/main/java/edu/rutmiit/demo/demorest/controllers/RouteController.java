package edu.rutmiit.demo.demorest.controllers;

import edu.rutmiit.demo.demorest.assemblers.RouteAssembler;
import edu.rutmiit.demo.demorest.service.RouteService;
import edu.rutmiit.demo.way_finder_contract.dto.PatchRouteRequest;
import edu.rutmiit.demo.way_finder_contract.dto.PutRouteRequest;
import edu.rutmiit.demo.way_finder_contract.dto.RouteRequest;
import edu.rutmiit.demo.way_finder_contract.dto.RouteResponse;
import edu.rutmiit.demo.way_finder_contract.endpoints.RouteApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class RouteController implements RouteApi {
    private static final Logger log = LoggerFactory.getLogger(RouteController.class);
    private final RouteService routeService;
    private final PagedResourcesAssembler<RouteResponse> pagedRouteAssembler;
    private final RouteAssembler routeAssembler;

    @Autowired
    public RouteController(RouteService routeService, PagedResourcesAssembler<RouteResponse> pagedResourcesAssembler, RouteAssembler routeAssembler) {
        this.routeService = routeService;
        this.pagedRouteAssembler = pagedResourcesAssembler;
        this.routeAssembler = routeAssembler;
    }

    @Override
    public EntityModel<RouteResponse> getRuoteById(Long id) {
        return routeAssembler.toModel(routeService.findById(id));
    }

    @Override
    public PagedModel<EntityModel<RouteResponse>> getAllRouts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RouteResponse> paged = routeService.findAll(pageable);
        Page<RouteResponse> springPage = new PageImpl<>(
                paged.getContent(),
                PageRequest.of(paged.getPageable().getPageNumber(), paged.getPageable().getPageSize()),
                paged.getTotalElements()
        );
        return pagedRouteAssembler.toModel(springPage, routeAssembler);
    }

    @Override
    public ResponseEntity<EntityModel<RouteResponse>> createRoute(RouteRequest request) {
        RouteResponse created = routeService.create(request);
        EntityModel<RouteResponse> model = routeAssembler.toModel(created);
        return ResponseEntity
                .created(model.getRequiredLink("self").toUri())
                .body(model);
    }

    @Override
    public EntityModel<RouteResponse> patchRoute(Long id, PatchRouteRequest request) {
        return routeAssembler.toModel(routeService.patch(request, id));
    }

    @Override
    public EntityModel<RouteResponse> putRoute(Long id, PutRouteRequest request) {
        return routeAssembler.toModel(routeService.put(request, id));
    }

    @Override
    public EntityModel<RouteResponse> deleteRoute(Long id) {
        return routeAssembler.toModel(routeService.delete(id));
    }
}
