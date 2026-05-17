package edu.rutmiit.demo.demorest.service;

import edu.rutmiit.demo.way_finder_contract.dto.HaltRequest;
import edu.rutmiit.demo.way_finder_contract.dto.HaltResponse;
import edu.rutmiit.demo.way_finder_contract.dto.PatchHaltRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface HaltService {
    Page<HaltResponse> findAll(Pageable pageable);
    HaltResponse findById(long id);
    HaltResponse create(HaltRequest haltRequest);
    HaltResponse patch(PatchHaltRequest request, long id);
    Page<HaltResponse> findAllByRoute(Pageable pageable, long routeId);
}
