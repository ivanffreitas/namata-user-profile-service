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
@Table(name = "user_saved_trails", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_profile_id", "trail_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSavedTrail {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;

    @Column(name = "trail_id", nullable = false)
    private UUID trailId; // ID da trilha (referência para trail-service)

    @CreationTimestamp
    @Column(name = "saved_at", nullable = false, updatable = false)
    private LocalDateTime savedAt;

    @Column(name = "notes", length = 500)
    private String notes; // Notas opcionais do usuário sobre a trilha salva

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true; // Para soft delete
}