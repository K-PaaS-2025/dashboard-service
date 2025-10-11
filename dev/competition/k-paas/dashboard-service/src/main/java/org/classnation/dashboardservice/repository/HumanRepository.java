package org.classnation.dashboardservice.repository;

import org.classnation.dashboardservice.entity.Human;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HumanRepository extends JpaRepository<Human, Long> {

    Optional<Human> findByHumanUuid(String humanUuid);

    boolean existsByHumanUuid(String humanUuid);
}
