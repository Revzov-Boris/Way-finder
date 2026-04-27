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
import edu.rutmiit.demo.way_finder_contract.dto.PatchHaltRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
        return haltRepository.findAll(pageable).map(e -> toResponse(e));
    }

    @Override
    public HaltResponse findById(long id) {
        HaltEntity haltEntity = getEntity(id);
        return toResponse(haltEntity);
    }

    @Override
    @Transactional
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


    @Override
    @Transactional
    public HaltResponse patch(PatchHaltRequest request, long id) {
        System.out.println("ДОШЁЛ до контроллера");
        HaltEntity entity = getEntity(id);
        System.out.println("ПОЛУЧИЛ enitity HALT");
        CityEntity cityEntity = null;
        if (request.getCityId() != null) {
            cityEntity = cityRepository.findById(request.getCityId()).orElseThrow(
                    () -> new EntityNotFoundException("Не найден город с ID=" + request.getCityId())
            );
        }
        System.out.println("ПОЛУЧИЛ 1");
        RouteEntity routeEntity = null;
        if (request.getRouteId() != null) {
            routeEntity = routeRepository.findById(request.getRouteId()).orElseThrow(
                    () -> new EntityNotFoundException("Не найден маршрут с ID=" + request.getRouteId())
            );
        }
        if (routeEntity != null)
            entity.setRoute(routeEntity);
        if (cityEntity != null)
            entity.setCity(cityEntity);
        if (request.getDate() != null) {
            entity.setTime(request.getDate());
        }
        entity = haltRepository.save(entity);
        return toResponse(entity);
    }


    public HaltEntity getEntity(long id) {
        return haltRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Нет остановки с ID=" + id)
        );
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
