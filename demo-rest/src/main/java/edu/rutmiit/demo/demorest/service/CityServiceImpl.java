package edu.rutmiit.demo.demorest.service;

import edu.rutmiit.demo.demorest.entities.CityEntity;
import edu.rutmiit.demo.demorest.events.CityEventPublisher;
import edu.rutmiit.demo.demorest.repositories.CityRepository;
import edu.rutmiit.demo.way_finder_contract.dto.CityRequest;
import edu.rutmiit.demo.way_finder_contract.dto.CityResponse;
import edu.rutmiit.demo.way_finder_contract.dto.PatchCityRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;
    private final CityEventPublisher eventPublisher;

    @Autowired
    public CityServiceImpl(CityRepository cityRepository, CityEventPublisher eventPublisher) {
        this.cityRepository = cityRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Page<CityResponse> findAll(Pageable pageable) {
        return cityRepository.findAll(pageable).map(e -> toResponse(e));
    }

    @Override
    public CityResponse findById(int id) {
        return toResponse(getEntityById(id));
    }

    @Override
    @Transactional
    public CityResponse create(CityRequest cityRequest) {
        if (cityRepository.findByAddress(cityRequest.getAddress()).isPresent()) {
            throw new EntityNotFoundException("Уже есть адрес такой");
        }
        CityEntity cityEntity = CityEntity.builder()
                .timeZone(cityRequest.getTimeZone())
                .name(cityRequest.getName())
                .address(cityRequest.getAddress())
                .build();
        cityEntity = cityRepository.save(cityEntity);
        CityResponse response = toResponse(cityEntity);
        cityRepository.flush();
        eventPublisher.publishCreated(response);
        return response;
    }


    @Override
    @Transactional
    public CityResponse patch(PatchCityRequest request, int id) {
        CityEntity entity = getEntityById(id);
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getAddress() != null) {
            entity.setAddress(request.getAddress());
        }
        if (request.getTimeZone() != null) {
            entity.setTimeZone(request.getTimeZone());
        }
        cityRepository.save(entity);
        cityRepository.flush();
        eventPublisher.publishPatchupdated(toResponse(entity));
        return toResponse(entity);
    }


    public CityEntity getEntityById(int id) {
        return cityRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Не найдена город с ID=" + id));
    }


    public static CityResponse toResponse(CityEntity entity) {
        return CityResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .address(entity.getAddress())
                .timeZone(entity.getTimeZone())
                .build();
    }
}
