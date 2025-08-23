package com.namata.userprofile.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "achievements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Achievement {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_id", nullable = false)
    private Badge badge;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "progress")
    private Integer progress; // Progresso atual (0-100)

    @Column(name = "is_completed")
    @Builder.Default
    private Boolean isCompleted = false;

    @CreationTimestamp
    @Column(name = "earned_at")
    private LocalDateTime earnedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // Metadados específicos da conquista
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON com dados específicos da conquista

    // Método para obter o progresso máximo (sempre 100 para conquistas)
    public Integer getMaxProgress() {
        return 100;
    }
}