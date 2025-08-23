package com.namata.userprofile.service;

import com.namata.userprofile.dto.ActivityDTO;
import com.namata.userprofile.entity.Activity;
import com.namata.userprofile.entity.UserProfile;
import com.namata.userprofile.repository.ActivityRepository;
import com.namata.userprofile.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserProfileRepository userProfileRepository;

    public ActivityDTO createActivity(UUID userId, ActivityDTO activityDTO) {
        log.info("Criando atividade para usuário ID: {}", userId);

        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de usuário não encontrado"));

        Activity activity = Activity.builder()
                .userProfile(userProfile)
                .type(activityDTO.getType())
                .title(activityDTO.getTitle())
                .description(activityDTO.getDescription())
                .trailId(activityDTO.getTrailId())
                .distance(activityDTO.getDistance())
                .duration(activityDTO.getDuration())
                .elevationGain(activityDTO.getElevationGain())
                .difficulty(activityDTO.getDifficulty())
                .location(activityDTO.getLocation())
                .photoUrls(activityDTO.getPhotoUrls())
                .likes(0)
                .comments(0)
                .isPublic(activityDTO.getIsPublic() != null ? activityDTO.getIsPublic() : true)
                .completedAt(activityDTO.getCompletedAt() != null ? activityDTO.getCompletedAt() : LocalDateTime.now())
                .build();

        Activity savedActivity = activityRepository.save(activity);
        log.info("Atividade criada com sucesso ID: {}", savedActivity.getId());

        return convertToDTO(savedActivity);
    }

    @Transactional(readOnly = true)
    public Optional<ActivityDTO> getActivityById(UUID activityId) {
        return activityRepository.findById(activityId)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<ActivityDTO> getUserActivities(UUID userId, Pageable pageable) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de usuário não encontrado"));

        return activityRepository.findByUserProfileOrderByCreatedAtDesc(userProfile, pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public List<ActivityDTO> getUserActivitiesByType(UUID userId, Activity.ActivityType type) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de usuário não encontrado"));

        return activityRepository.findByUserProfileAndType(userProfile, type)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ActivityDTO> getPublicActivities() {
        return activityRepository.findByIsPublicTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ActivityDTO> getRecentActivities(int limit) {
        return activityRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .limit(limit)
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ActivityDTO> getActivitiesByTrail(UUID trailId) {
        return activityRepository.findByTrailIdOrderByCreatedAtDesc(trailId)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public ActivityDTO updateActivity(UUID activityId, ActivityDTO activityDTO) {
        log.info("Atualizando atividade ID: {}", activityId);

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Atividade não encontrada"));

        // Atualizar apenas campos não nulos
        if (activityDTO.getTitle() != null) {
            activity.setTitle(activityDTO.getTitle());
        }
        if (activityDTO.getDescription() != null) {
            activity.setDescription(activityDTO.getDescription());
        }
        if (activityDTO.getDistance() != null) {
            activity.setDistance(activityDTO.getDistance());
        }
        if (activityDTO.getDuration() != null) {
            activity.setDuration(activityDTO.getDuration());
        }
        if (activityDTO.getElevationGain() != null) {
            activity.setElevationGain(activityDTO.getElevationGain());
        }
        if (activityDTO.getDifficulty() != null) {
            activity.setDifficulty(activityDTO.getDifficulty());
        }
        if (activityDTO.getLocation() != null) {
            activity.setLocation(activityDTO.getLocation());
        }
        if (activityDTO.getPhotoUrls() != null) {
            activity.setPhotoUrls(activityDTO.getPhotoUrls());
        }
        if (activityDTO.getIsPublic() != null) {
            activity.setIsPublic(activityDTO.getIsPublic());
        }

        Activity updatedActivity = activityRepository.save(activity);
        log.info("Atividade atualizada com sucesso ID: {}", activityId);

        return convertToDTO(updatedActivity);
    }

    public void deleteActivity(UUID activityId) {
        log.info("Deletando atividade ID: {}", activityId);

        if (!activityRepository.existsById(activityId)) {
            throw new IllegalArgumentException("Atividade não encontrada");
        }

        activityRepository.deleteById(activityId);
        log.info("Atividade deletada com sucesso ID: {}", activityId);
    }

    public ActivityDTO likeActivity(UUID activityId) {
        log.info("Curtindo atividade ID: {}", activityId);

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Atividade não encontrada"));

        activity.setLikes(activity.getLikes() + 1);
        Activity updatedActivity = activityRepository.save(activity);

        log.info("Atividade curtida com sucesso ID: {}", activityId);
        return convertToDTO(updatedActivity);
    }

    public ActivityDTO addComment(UUID activityId) {
        log.info("Adicionando comentário à atividade ID: {}", activityId);

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Atividade não encontrada"));

        activity.setComments(activity.getComments() + 1);
        Activity updatedActivity = activityRepository.save(activity);

        log.info("Comentário adicionado à atividade ID: {}", activityId);
        return convertToDTO(updatedActivity);
    }

    @Transactional(readOnly = true)
    public long countUserActivities(UUID userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Perfil de usuário não encontrado"));

        return activityRepository.countByUserProfile(userProfile);
    }

    @Transactional(readOnly = true)
    public long countUserActivitiesByType(UUID userId, Activity.ActivityType type) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Perfil de usuário não encontrado"));

        return activityRepository.countByUserProfileAndType(userProfile, type);
    }

    @Transactional(readOnly = true)
    public Double getTotalDistanceByUser(UUID userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de usuário não encontrado"));

        return activityRepository.sumDistanceByUserProfile(userProfile);
    }

    @Transactional(readOnly = true)
    public Integer getTotalDurationByUser(UUID userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de usuário não encontrado"));

        return activityRepository.sumDurationByUserProfile(userProfile);
    }

    private ActivityDTO convertToDTO(Activity activity) {
        return ActivityDTO.builder()
                .id(activity.getId())
                .userProfileId(activity.getUserProfile().getId())
                .userDisplayName(activity.getUserProfile().getDisplayName())
                .userProfilePictureUrl(activity.getUserProfile().getProfilePictureUrl())
                .type(activity.getType())
                .title(activity.getTitle())
                .description(activity.getDescription())
                .trailId(activity.getTrailId())
                .distance(activity.getDistance())
                .duration(activity.getDuration())
                .elevationGain(activity.getElevationGain())
                .difficulty(activity.getDifficulty())
                .location(activity.getLocation())
                .photoUrls(activity.getPhotoUrls())
                .likes(activity.getLikes())
                .comments(activity.getComments())
                .isPublic(activity.getIsPublic())
                .createdAt(activity.getCreatedAt())
                .completedAt(activity.getCompletedAt())
                .build();
    }
}