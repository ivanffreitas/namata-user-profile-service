package com.namata.userprofile.dto;

import com.namata.userprofile.entity.UserProfile;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserProfileRequest {

    @Size(max = 100, message = "Nome de exibição deve ter no máximo 100 caracteres")
    private String displayName;

    @Size(max = 500, message = "Bio deve ter no máximo 500 caracteres")
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
}