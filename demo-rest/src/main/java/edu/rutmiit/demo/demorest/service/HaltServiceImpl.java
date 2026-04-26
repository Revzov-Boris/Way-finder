package edu.rutmiit.demo.demorest.service;

import edu.rutmiit.demo.demorest.controllers.RouteController;
import edu.rutmiit.demo.demorest.entities.CityEntity;
import edu.rutmiit.demo.demorest.entities.HaltEntity;
import edu.rutmiit.demo.demorest.entities.RouteEntity;
import edu.rutmiit.demo.demorest.repositories.CityRepository;
import edu.rutmiit.demo.demorest.repositories.HaltRepository;
import edu.rutmiit.demo.demorest.repositories.RouteRepository;
import edu.rutmiit.demo.way_finder_contract.dto.HaltRequest;
import edu.rutmiit.demo.way_finder_contract.dto.HaltResponse;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class HaltServiceImpl implements HaltService {
    private final HaltRepository haltRepository;
    private final CityRepository cityRepository;
    private final RouteRepository routeRepository;

    public HaltServiceImpl(HaltRepository haltRepository, CityRepository cityRepository, RouteRepository routeRepository) {
        this.haltRepository = haltRepository;
        this.cityRepository = cityRepository;
        this.routeRepository = routeRepository;
    }

    @Override
    public Page<HaltResponse> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public HaltResponse findById(long id) {
        return null;
    }

    @Override
    public HaltResponse create(HaltRequest haltRequest) {
        CityEntity cityEntity = cityRepository.findById(haltRequest.getCityId()).orElseThrow(
                () -> new EntityNotFoundException("Не найден город с ID=" + haltRequest.getCityId())
        );
        RouteEntity routeEntity = routeRepository.findById(haltRequest.getRouteId()).orElseThrow(
                () -> new EntityNotFoundException("Не найден маршрут с ID=" + haltRequest.getRouteId())
        );
        HaltEntity entity = HaltEntity.builder()
                .city(cityEntity)
                .time(haltRequest.getDate())
                .route(routeEntity)
                .build();
        entity = haltRepository.save(entity);
        return toResponse(entity);
    }


    public static HaltResponse toResponse(HaltEntity entity) {
        return HaltResponse.builder()
                .id(entity.getId())
                .cityId(entity.getCity().getId())
                .routeId(entity.getRoute().getId())
                .date(entity.getTime())
                .build();
    }
}
