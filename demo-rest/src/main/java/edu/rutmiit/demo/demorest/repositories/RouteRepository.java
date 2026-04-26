package edu.rutmiit.demo.demorest.repositories;

import edu.rutmiit.demo.demorest.entities.RouteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<RouteEntity, Long> {
    Page<RouteEntity> findAll(Pageable pageable);
}
