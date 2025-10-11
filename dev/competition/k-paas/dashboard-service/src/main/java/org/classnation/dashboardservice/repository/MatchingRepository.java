package org.classnation.dashboardservice.repository;

import org.classnation.dashboardservice.entity.Matching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchingRepository extends JpaRepository<Matching, Long> {

    boolean existsByHumanUuidAndDogUuid(String humanUuid, String dogUuid);
}
