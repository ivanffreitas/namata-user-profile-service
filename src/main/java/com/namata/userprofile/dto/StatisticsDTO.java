package com.namata.userprofile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDTO {
    private UUID id;
    private UUID userProfileId;
    
    // Estatísticas de trilhas
    private Integer totalTrailsCompleted;
    private Double totalDistanceKm;
    private Integer totalTimeMinutes;
    private Double totalElevationGainM;
    private Integer longestTrailKm;
    private Integer highestElevationM;
    
    // Estatísticas de atividade
    private Integer totalPhotosShared;
    private Integer totalReviewsPosted;
    private Integer totalLikesReceived;
    private Integer totalCommentsReceived;
    
    // Estatísticas de conquistas
    private Integer totalBadgesEarned;
    private Integer totalPoints;
    private Integer currentStreak;
    private Integer longestStreak;
    
    // Estatísticas sociais
    private Integer totalFollowers;
    private Integer totalFollowing;
    private Integer totalGuidesBooked;
    
    // Ranking
    private Integer globalRank;
    private Integer localRank;
    
    private LocalDateTime lastActivityAt;
    private LocalDateTime updatedAt;
}