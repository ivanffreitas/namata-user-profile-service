package com.namata.userprofile.dto;

import com.namata.userprofile.entity.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private UUID id;
    private UUID userId;
    private String firstName; // Nome do usuário do auth-service
    private String displayName;
    private String bio;
    private String profilePictureUrl;
    private LocalDate dateOfBirth;
    private UserProfile.Gender gender;
    private String location;
    private String phoneNumber;
    private UserProfile.ExperienceLevel experienceLevel;
    private List<UserProfile.Interest> interests;
    private UserProfile.ExplorationType explorationType;
    private UserProfile.PrivacyLevel privacyLevel;
    private Boolean isActive;
    private Boolean isVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Estatísticas básicas
    private Integer totalTrailsCompleted;
    private Integer totalBadgesEarned;
    private Integer totalPoints;
}