package com.namata.userprofile.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "badges")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Badge {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "name", unique = true, nullable = false, length = 100)
    private String name;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "icon_url", nullable = false)
    private String iconUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    @Builder.Default
    private BadgeType type = BadgeType.ACHIEVEMENT;

    @Enumerated(EnumType.STRING)
    @Column(name = "rarity")
    @Builder.Default
    private Rarity rarity = Rarity.COMMON;

    @Column(name = "points_required")
    private Integer pointsRequired; // Pontos necessários para conquistar

    @Column(name = "max_progress")
    private Integer maxProgress; // Progresso máximo (ex: 100 para 100%)

    @Column(name = "criteria", length = 1000)
    private String criteria; // Critérios para conquistar o badge

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relacionamentos
    @OneToMany(mappedBy = "badge", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Achievement> achievements;

    public enum BadgeType {
        TRAIL, DISTANCE, ELEVATION, SOCIAL, SPECIAL, ACHIEVEMENT, MILESTONE, SPECIAL_EVENT, SEASONAL, COMMUNITY
    }

    public enum Rarity {
        COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
    }
}