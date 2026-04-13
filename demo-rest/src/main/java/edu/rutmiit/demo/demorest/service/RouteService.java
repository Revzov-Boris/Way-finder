package edu.rutmiit.demo.demorest.service;

import edu.rutmiit.demo.way_finder_contract.dto.RouteRequest;
import edu.rutmiit.demo.way_finder_contract.dto.RouteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RouteService {
    Page<RouteResponse> findAll(Pageable pageable);
    RouteResponse findById(long id);
    RouteResponse create(RouteRequest routeRequest);
}
