package edu.rutmiit.demo.demorest.repositories;

import edu.rutmiit.demo.demorest.entities.HaltEntity;
import edu.rutmiit.demo.way_finder_contract.dto.HaltResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HaltRepository extends JpaRepository<HaltEntity, Long> {
    Page<HaltEntity> findAll(Pageable pageable);
}
