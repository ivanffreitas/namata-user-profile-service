package com.namata.userprofile.dto;

import com.namata.userprofile.entity.Activity;
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
public class ActivityDTO {
    private UUID id;
    private UUID userProfileId;
    private String userDisplayName;
    private String userProfilePictureUrl;
    private Activity.ActivityType type;
    private String title;
    private String description;
    private UUID trailId;
    private Double distance;
    private Integer duration;
    private Double elevationGain;
    private Integer difficulty;
    private String location;
    private String photoUrls;
    private Integer likes;
    private Integer comments;
    private Boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}