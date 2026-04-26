package edu.rutmiit.demo.demorest.service;

import edu.rutmiit.demo.demorest.entities.RouteEntity;
import edu.rutmiit.demo.demorest.repositories.RouteRepository;
import edu.rutmiit.demo.way_finder_contract.dto.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

@Service
public class RouteServiceImpl implements RouteService {
    private final RouteRepository routeRepository;

    @Autowired
    public RouteServiceImpl(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @Override
    public Page<RouteResponse> findAll(Pageable pageable) {
        System.out.println("SERVICE");
        return routeRepository.findAll(pageable).map(e -> toResponse(e));
    }

    @Override
    public RouteResponse findById(long id) {
        RouteEntity routeEntity = getEntity(id);
        return toResponse(routeEntity);
    }

    @Override
    @Transactional
    public RouteResponse create(RouteRequest routeRequest) {
        RouteEntity routeEntity = RouteEntity.builder()
                .typeTransport(routeRequest.getTypeTransport())
                .typeDistance(routeRequest.getTypeDistance())
                .build();
        routeEntity = routeRepository.save(routeEntity);
        return toResponse(routeEntity);
    }


    public static RouteResponse toResponse(RouteEntity entity) {
        if (entity.getHalts() == null) entity.setHalts(new ArrayList<>());
        return RouteResponse.builder()
                .id(entity.getId())
                .typeTransport(entity.getTypeTransport())
                .typeDistance(entity.getTypeDistance())
                .halts(entity.getHalts().stream().map(e -> {
                    HaltResponse h = HaltResponse.builder()
                            .id(e.getId())
                            .cityId(e.getCity().getId())
                            .routeId(e.getRoute().getId())
                            .date(e.getTime())
                            .build();
                    return h;
                }).sorted(Comparator.comparing(HaltResponse::getDate)).toList())
                .build();
    }


    public RouteEntity getEntity(long id) {
        return routeRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Не найден маршрут с ID=" + id)
        );

    }


    @Override
    @Transactional
    public RouteResponse patch(PatchRouteRequest request, long id) {
        RouteEntity entity = getEntity(id);
        if (request.getTypeDistance() != null) {
            entity.setTypeDistance(request.getTypeDistance());
        }
        if (request.getTypeTransport() != null) {
            entity.setTypeTransport(request.getTypeTransport());
        }
        entity = routeRepository.save(entity);
        return toResponse(entity);
    }


    @Override
    @Transactional
    public RouteResponse put(PutRouteRequest request, long id) {
        RouteEntity entity = getEntity(id);
        entity.setTypeTransport(request.getTypeTransport());
        entity.setTypeDistance(request.getTypeDistance());
        entity = routeRepository.save(entity);
        return toResponse(entity);
    }


    @Override
    @Transactional
    public RouteResponse delete(long id) {
        RouteEntity entity = getEntity(id);
        RouteResponse response = toResponse(entity);
        routeRepository.delete(entity);
        return response;
    }
}
