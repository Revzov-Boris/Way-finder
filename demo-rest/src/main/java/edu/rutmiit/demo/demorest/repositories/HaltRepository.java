package edu.rutmiit.demo.demorest.repositories;

import edu.rutmiit.demo.demorest.entities.HaltEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HaltRepository extends JpaRepository<HaltEntity, Long> {
}
