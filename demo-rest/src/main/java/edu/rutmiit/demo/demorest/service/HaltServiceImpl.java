package edu.rutmiit.demo.demorest.service;

import edu.rutmiit.demo.demorest.entities.CityEntity;
import edu.rutmiit.demo.demorest.entities.HaltEntity;
import edu.rutmiit.demo.demorest.entities.RouteEntity;
import edu.rutmiit.demo.demorest.events.HaltEventPublisher;
import edu.rutmiit.demo.demorest.repositories.CityRepository;
import edu.rutmiit.demo.demorest.repositories.HaltRepository;
import edu.rutmiit.demo.demorest.repositories.RouteRepository;
import edu.rutmiit.demo.way_finder_contract.dto.HaltRequest;
import edu.rutmiit.demo.way_finder_contract.dto.HaltResponse;
import edu.rutmiit.demo.way_finder_contract.dto.PatchHaltRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class HaltServiceImpl implements HaltService {
    private final HaltRepository haltRepository;
    private final CityRepository cityRepository;
    private final RouteRepository routeRepository;
    private final HaltEventPublisher eventPublisher;

    public HaltServiceImpl(HaltRepository haltRepository, CityRepository cityRepository, RouteRepository routeRepository, HaltEventPublisher eventPublisher) {
        this.haltRepository = haltRepository;
        this.cityRepository = cityRepository;
        this.routeRepository = routeRepository;
        this.eventPublisher = eventPublisher;
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
        HaltResponse response = toResponse(entity);
        eventPublisher.publishCreated(response);
        return response;
    }


    @Override
    @Transactional
    public HaltResponse patch(PatchHaltRequest request, long id) {
        HaltEntity entity = getEntity(id);
        CityEntity cityEntity = null;
        if (request.getCityId() != null) {
            cityEntity = cityRepository.findById(request.getCityId()).orElseThrow(
                    () -> new EntityNotFoundException("Не найден город с ID=" + request.getCityId())
            );
        }
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
        HaltResponse response = toResponse(entity);
        eventPublisher.publishPatchupdated(response);
        return response;
    }


    public HaltEntity getEntity(long id) {
        return haltRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Нет остановки с ID=" + id)
        );
    }


    @Override
    public Page<HaltResponse> findAllByRoute(Pageable pageable, long routeId) {
        return haltRepository.findByRouteId(routeId, pageable).map(e -> toResponse(e));
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
