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
@Table(name = "activities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Activity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ActivityType type;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "trail_id")
    private UUID trailId; // ID da trilha (referência para trail-service)

    @Column(name = "distance")
    private Double distance; // em quilômetros

    @Column(name = "duration")
    private Integer duration; // em minutos

    @Column(name = "elevation_gain")
    private Double elevationGain; // em metros

    @Column(name = "difficulty")
    private Integer difficulty; // 1-5

    @Column(name = "location")
    private String location;

    @Column(name = "photo_urls")
    private String photoUrls; // URLs das fotos separadas por vírgula

    @Column(name = "likes")
    @Builder.Default
    private Integer likes = 0;

    @Column(name = "comments")
    @Builder.Default
    private Integer comments = 0;

    @Column(name = "is_public")
    @Builder.Default
    private Boolean isPublic = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public enum ActivityType {
        TRAIL_COMPLETED, PHOTO_SHARED, ACHIEVEMENT_EARNED, 
        REVIEW_POSTED, GUIDE_BOOKED, LOCATION_CHECKED_IN
    }
}