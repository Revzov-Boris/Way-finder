package edu.rutmiit.demo.demorest.service;

import edu.rutmiit.demo.demorest.repositories.HaltRepository;
import edu.rutmiit.demo.way_finder_contract.dto.HaltRequest;
import edu.rutmiit.demo.way_finder_contract.dto.HaltResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class HaltServiceImpl implements HaltService {
    private final HaltRepository haltRepository;

    public HaltServiceImpl(HaltRepository haltRepository) {
        this.haltRepository = haltRepository;
    }

    @Override
    public Page<HaltResponse> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public HaltResponse findById(int id) {
        return null;
    }

    @Override
    public HaltResponse create(HaltRequest cityRequest) {
        return null;
    }
}
