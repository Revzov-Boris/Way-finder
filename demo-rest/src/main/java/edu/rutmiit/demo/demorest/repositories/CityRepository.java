package edu.rutmiit.demo.demorest.repositories;

import edu.rutmiit.demo.demorest.entities.CityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CityRepository extends JpaRepository<CityEntity, Integer> {
    Page<CityEntity> findAll(Pageable pageable);
    Optional<CityEntity> findByAddress(String address);
}
