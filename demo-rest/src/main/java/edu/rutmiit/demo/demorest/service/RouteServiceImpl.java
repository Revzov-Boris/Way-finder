package edu.rutmiit.demo.demorest.service;

import edu.rutmiit.demo.demorest.repositories.RouteRepository;
import edu.rutmiit.demo.way_finder_contract.dto.RouteRequest;
import edu.rutmiit.demo.way_finder_contract.dto.RouteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RouteServiceImpl implements RouteService {
    private final RouteRepository routeRepository;

    @Autowired
    public RouteServiceImpl(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @Override
    public Page<RouteResponse> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public RouteResponse findById(long id) {
        return null;
    }

    @Override
    public RouteResponse create(RouteRequest routeRequest) {
        return null;
    }
}
