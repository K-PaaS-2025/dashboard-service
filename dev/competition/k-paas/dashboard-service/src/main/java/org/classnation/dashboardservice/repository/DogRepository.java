package org.classnation.dashboardservice.repository;

import org.classnation.dashboardservice.entity.Dog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DogRepository extends JpaRepository<Dog, Long> {

    Optional<Dog> findByDogUuid(String dogUuid);

    boolean existsByDogUuid(String dogUuid);
}
