package com.yourcode.mirae.speedrun.repository;

import com.yourcode.mirae.speedrun.entity.SpeedrunSession;
import com.yourcode.mirae.speedrun.redis.GameMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpeedrunSessionRepository extends JpaRepository<SpeedrunSession, Long> {

    Optional<SpeedrunSession> findBySessionId(String sessionId);

    List<SpeedrunSession> findByCreatedBy(Long userId);

    Page<SpeedrunSession> findByMode(GameMode mode, Pageable pageable);

    List<SpeedrunSession> findByEndedAtBetween(LocalDateTime start, LocalDateTime end);

    long countByCreatedBy(Long userId);
}
