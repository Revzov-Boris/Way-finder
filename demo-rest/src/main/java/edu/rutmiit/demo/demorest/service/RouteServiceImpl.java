package edu.rutmiit.demo.demorest.service;

import edu.rutmiit.demo.demorest.entities.RouteEntity;
import edu.rutmiit.demo.demorest.events.RouteEventPublisher;
import edu.rutmiit.demo.demorest.repositories.RouteRepository;
import edu.rutmiit.demo.way_finder_contract.dto.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Comparator;

@Service
public class RouteServiceImpl implements RouteService {
    private final RouteRepository routeRepository;
    private final RouteEventPublisher eventPublisher;

    @Autowired
    public RouteServiceImpl(RouteRepository routeRepository, RouteEventPublisher eventPublisher) {
        this.routeRepository = routeRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Page<RouteResponse> findAll(Pageable pageable) {
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
        RouteResponse response = toResponse(routeEntity);
        eventPublisher.publishCreated(response);
        return response;
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
        RouteResponse response = toResponse(entity);
        eventPublisher.publishPatchupdated(response);
        return response;
    }


    @Override
    @Transactional
    public RouteResponse put(PutRouteRequest request, long id) {
        RouteEntity entity = getEntity(id);
        entity.setTypeTransport(request.getTypeTransport());
        entity.setTypeDistance(request.getTypeDistance());
        entity = routeRepository.save(entity);
        RouteResponse response = toResponse(entity);
        eventPublisher.publishUpdated(response);
        return response;
    }


    @Override
    @Transactional
    public RouteResponse delete(long id) {
        RouteEntity entity = getEntity(id);
        RouteResponse response = toResponse(entity);
        routeRepository.delete(entity);
        eventPublisher.publishDeleted(response);
        return response;
    }
}
