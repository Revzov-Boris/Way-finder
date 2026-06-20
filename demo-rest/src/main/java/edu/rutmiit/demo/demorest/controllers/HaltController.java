package edu.rutmiit.demo.demorest.controllers;

import edu.rutmiit.demo.demorest.assemblers.HaltAssembler;
import edu.rutmiit.demo.demorest.service.HaltService;
import edu.rutmiit.demo.way_finder_contract.dto.*;
import edu.rutmiit.demo.way_finder_contract.endpoints.HaltApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;

@RestController
public class HaltController implements HaltApi {
    private static final Logger log = LoggerFactory.getLogger(HaltController.class);
    private final HaltService haltService;
    private final PagedResourcesAssembler<HaltResponse> pageHaltAssembler;
    private final HaltAssembler haltAssembler;

    public HaltController(HaltService haltService, PagedResourcesAssembler<HaltResponse> pageHaltAssembler, HaltAssembler haltAssembler) {
        this.haltService = haltService;
        this.pageHaltAssembler = pageHaltAssembler;
        this.haltAssembler = haltAssembler;
    }

    @Override
    public EntityModel<HaltResponse> getHaltById(Long id) {
        return haltAssembler.toModel(haltService.findById(id));
    }

    @Override
    public PagedModel<EntityModel<HaltResponse>> getAllHalts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<HaltResponse> paged = haltService.findAll(pageable);
        Page<HaltResponse> springPage = new PageImpl<>(
                paged.getContent(),
                PageRequest.of(paged.getPageable().getPageNumber(), paged.getPageable().getPageSize()),
                paged.getTotalElements()
        );
        return pageHaltAssembler.toModel(springPage, haltAssembler);
    }

    @Override
    public ResponseEntity<EntityModel<HaltResponse>> createHalt(HaltRequest request) {
        HaltResponse created = haltService.create(request);
        EntityModel<HaltResponse> model = haltAssembler.toModel(created);
        return ResponseEntity
                .created(model.getRequiredLink("self").toUri())
                .body(model);
    }


    @Override
    public EntityModel<HaltResponse> patchHalt(PatchHaltRequest request, long id) {
        return haltAssembler.toModel(haltService.patch(request, id));
    }


    @Override
    public EntityModel<HaltResponse> deleteHalt(long id) {
        return haltAssembler.toModel(haltService.delete(id));
    }
}
