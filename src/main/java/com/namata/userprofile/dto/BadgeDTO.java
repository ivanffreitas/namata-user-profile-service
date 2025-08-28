package com.namata.userprofile.dto;

import com.namata.userprofile.entity.Badge;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BadgeDTO {
    private UUID id;
    private String name;
    private String description;
    private String iconUrl;
    private Badge.BadgeType type;
    private Badge.Rarity rarity;
    private Integer pointsRequired;
    private Integer maxProgress;
    
    public static BadgeDTO fromEntity(Badge badge) {
        return new BadgeDTO(
            badge.getId(),
            badge.getName(),
            badge.getDescription(),
            badge.getIconUrl(),
            badge.getType(),
            badge.getRarity(),
            badge.getPointsRequired(),
            badge.getMaxProgress()
        );
    }
}