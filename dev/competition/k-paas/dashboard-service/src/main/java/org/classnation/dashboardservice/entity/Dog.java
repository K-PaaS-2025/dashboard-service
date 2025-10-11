package org.classnation.dashboardservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "dogs", indexes = {
        @Index(name = "idx_dog_status", columnList = "is_adopted,created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dog_uuid", length = 36, nullable = false, unique = true)
    private String dogUuid;

    @Column(name = "shelter_name", length = 100)
    private String shelterName;

    @Column(name = "name", length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "size")
    private DogSize size;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity")
    private Activity activity;

    @Enumerated(EnumType.STRING)
    @Column(name = "temperament")
    private Temperament temperament;

    @Column(name = "diseases", length = 255)
    private String diseases;

    @Column(name = "is_adopted", nullable = false)
    @Builder.Default
    private Boolean isAdopted = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isAdopted == null) {
            isAdopted = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
