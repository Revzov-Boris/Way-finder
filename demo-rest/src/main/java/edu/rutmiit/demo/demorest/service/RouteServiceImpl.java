package edu.rutmiit.demo.demorest.service;

import edu.rutmiit.demo.demorest.entities.RouteEntity;
import edu.rutmiit.demo.demorest.repositories.RouteRepository;
import edu.rutmiit.demo.way_finder_contract.dto.HaltResponse;
import edu.rutmiit.demo.way_finder_contract.dto.RouteRequest;
import edu.rutmiit.demo.way_finder_contract.dto.RouteResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

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
        RouteEntity routeEntity = routeRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Не найден маршрут с ID=" + id)
        );
        return toResponse(routeEntity);
    }

    @Override
    public RouteResponse create(RouteRequest routeRequest) {
        RouteEntity routeEntity = RouteEntity.builder()
                .type(routeRequest.getType())
                .build();
        System.out.println("Создал в БД");
        routeEntity = routeRepository.save(routeEntity);
        return toResponse(routeEntity);
    }


    public static RouteResponse toResponse(RouteEntity entity) {
        System.out.println("ОТСАНОВКИ: " + entity.getHalts());
        if (entity.getHalts() == null) entity.setHalts(new ArrayList<>());
        return RouteResponse.builder()
                .id(entity.getId())
                .type(entity.getType())
                .halts(entity.getHalts().stream().map(e -> {
                    HaltResponse h = HaltResponse.builder()
                            .id(e.getId())
                            .cityId(e.getCity().getId())
                            .routeId(e.getRoute().getId())
                            .date(e.getTime())
                            .build();
                    return h;
                }).toList())
                .build();

    }
}
