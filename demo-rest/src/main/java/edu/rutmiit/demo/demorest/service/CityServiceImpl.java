package edu.rutmiit.demo.demorest.service;

import edu.rutmiit.demo.demorest.entities.CityEntity;
import edu.rutmiit.demo.demorest.events.CityEventPublisher;
import edu.rutmiit.demo.demorest.repositories.CityRepository;
import edu.rutmiit.demo.way_finder_contract.dto.CityRequest;
import edu.rutmiit.demo.way_finder_contract.dto.CityResponse;
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
        return toResponse(cityRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Не найдена город с ID=" + id))
        );
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
        eventPublisher.publishCreated(response);
        return response;
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
