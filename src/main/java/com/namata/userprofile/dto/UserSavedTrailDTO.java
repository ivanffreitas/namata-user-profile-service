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
public class UserSavedTrailDTO {
    private UUID id;
    private UUID userProfileId;
    private String userDisplayName;
    private String userProfilePictureUrl;
    private UUID trailId;
    private String notes;
    private LocalDateTime savedAt;
    private Boolean isActive;
    
    // Detalhes da trilha obtidos do trail-service
    private String trailName;
    private String trailDescription;
    private String difficultyLevel;
    private String distanceKm;
    private String durationHours;
}