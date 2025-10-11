package org.classnation.dashboardservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "humans", indexes = {
        @Index(name = "idx_human_status", columnList = "is_matched,created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Human {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "human_uuid", length = 36, nullable = false, unique = true)
    private String humanUuid;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "contact", length = 50)
    private String contact;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "home_size")
    private HomeSize homeSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "mobility")
    private Mobility mobility;

    @Column(name = "pet_experience")
    private Boolean petExperience;

    @Column(name = "outing_hours")
    private Integer outingHours;

    @Column(name = "initial_consulted", nullable = false)
    @Builder.Default
    private Boolean initialConsulted = false;

    @Column(name = "is_matched", nullable = false)
    @Builder.Default
    private Boolean isMatched = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (initialConsulted == null) {
            initialConsulted = false;
        }
        if (isMatched == null) {
            isMatched = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
