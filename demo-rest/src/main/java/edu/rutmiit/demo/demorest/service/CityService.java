package edu.rutmiit.demo.demorest.service;

import edu.rutmiit.demo.way_finder_contract.dto.CityRequest;
import edu.rutmiit.demo.way_finder_contract.dto.CityResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CityService {
    Page<CityResponse> findAll(Pageable pageable);
    CityResponse findById(int id);
    CityResponse create(CityRequest cityRequest);
}
