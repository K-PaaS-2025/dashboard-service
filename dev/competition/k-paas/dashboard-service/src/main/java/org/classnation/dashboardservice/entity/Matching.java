package org.classnation.dashboardservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "matchings",
        uniqueConstraints = {
                @UniqueConstraint(name = "uniq_human_dog", columnNames = {"human_uuid", "dog_uuid"})
        },
        indexes = {
                @Index(name = "idx_matched_at", columnList = "matched_at DESC")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Matching {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matching_id")
    private Long matchingId;

    @Column(name = "human_uuid", length = 36, nullable = false)
    private String humanUuid;

    @Column(name = "dog_uuid", length = 36, nullable = false)
    private String dogUuid;

    @Column(name = "report_id", length = 36, nullable = false)
    private String reportId;

    @Column(name = "is_danger", nullable = false)
    @Builder.Default
    private Boolean isDanger = false;

    @Column(name = "matched_at", nullable = false)
    private LocalDateTime matchedAt;

    @PrePersist
    protected void onCreate() {
        if (matchedAt == null) {
            matchedAt = LocalDateTime.now();
        }
    }
}
