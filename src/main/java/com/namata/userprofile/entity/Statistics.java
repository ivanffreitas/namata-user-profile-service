package com.namata.userprofile.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "statistics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Statistics {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false)
    @JsonIgnore
    private UserProfile userProfile;

    // Estatísticas de trilhas
    @Column(name = "total_trails_completed")
    @Builder.Default
    private Integer totalTrailsCompleted = 0;

    @Column(name = "total_distance_km")
    @Builder.Default
    private Double totalDistanceKm = 0.0;

    @Column(name = "total_time_minutes")
    @Builder.Default
    private Integer totalTimeMinutes = 0;

    @Column(name = "total_elevation_gain_m")
    @Builder.Default
    private Double totalElevationGainM = 0.0;

    @Column(name = "longest_trail_km")
    @Builder.Default
    private Integer longestTrailKm = 0;

    @Column(name = "highest_elevation_m")
    @Builder.Default
    private Integer highestElevationM = 0;

    // Estatísticas de atividade
    @Column(name = "total_photos_shared")
    @Builder.Default
    private Integer totalPhotosShared = 0;

    @Column(name = "total_reviews_posted")
    @Builder.Default
    private Integer totalReviewsPosted = 0;

    @Column(name = "total_likes_received")
    @Builder.Default
    private Integer totalLikesReceived = 0;

    @Column(name = "total_comments_received")
    @Builder.Default
    private Integer totalCommentsReceived = 0;

    // Estatísticas de conquistas
    @Column(name = "total_badges_earned")
    @Builder.Default
    private Integer totalBadgesEarned = 0;

    @Column(name = "total_points")
    @Builder.Default
    private Integer totalPoints = 0;

    @Column(name = "current_streak")
    @Builder.Default
    private Integer currentStreak = 0; // Dias consecutivos de atividade

    @Column(name = "longest_streak")
    @Builder.Default
    private Integer longestStreak = 0;

    // Estatísticas sociais
    @Column(name = "total_followers")
    @Builder.Default
    private Integer totalFollowers = 0;

    @Column(name = "total_following")
    @Builder.Default
    private Integer totalFollowing = 0;

    @Column(name = "total_guides_booked")
    @Builder.Default
    private Integer totalGuidesBooked = 0;

    // Ranking
    @Column(name = "global_rank")
    @Builder.Default
    private Integer globalRank = 0;

    @Column(name = "local_rank")
    @Builder.Default
    private Integer localRank = 0;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;
}