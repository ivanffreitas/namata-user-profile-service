package com.namata.userprofile.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.namata.userprofile.entity.Achievement;
import com.namata.userprofile.entity.Badge;
import com.namata.userprofile.entity.UserProfile;
import com.namata.userprofile.repository.AchievementRepository;
import com.namata.userprofile.repository.BadgeRepository;
import com.namata.userprofile.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final BadgeRepository badgeRepository;
    private final UserProfileRepository userProfileRepository;
    private final ObjectMapper objectMapper;

    public Achievement createAchievement(UUID userId, UUID badgeId, String description, 
                                       Integer maxProgress, Map<String, Object> metadata) {
        log.info("Criando conquista para usuário ID: {} e insígnia ID: {}", userId, badgeId);

        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de usuário não encontrado"));

        Badge badge = badgeRepository.findById(badgeId)
                .orElseThrow(() -> new IllegalArgumentException("Insígnia não encontrada"));

        // Verificar se já existe conquista para este usuário e insígnia
        if (achievementRepository.existsByUserProfileAndBadge(userProfile, badge)) {
            throw new IllegalArgumentException("Conquista já existe para este usuário e insígnia");
        }

        String metadataJson = null;
        if (metadata != null) {
            try {
                metadataJson = objectMapper.writeValueAsString(metadata);
            } catch (JsonProcessingException e) {
                log.error("Erro ao converter metadata para JSON", e);
                throw new RuntimeException("Erro ao processar metadata", e);
            }
        }

        Achievement achievement = Achievement.builder()
                .userProfile(userProfile)
                .badge(badge)
                .description(description)
                .progress(0)
                .isCompleted(false)
                .metadata(metadataJson)
                .build();

        Achievement savedAchievement = achievementRepository.save(achievement);
        log.info("Conquista criada com sucesso ID: {}", savedAchievement.getId());

        return savedAchievement;
    }

    @Transactional(readOnly = true)
    public Optional<Achievement> getAchievementById(UUID achievementId) {
        return achievementRepository.findById(achievementId);
    }

    @Transactional(readOnly = true)
    public List<Achievement> getUserAchievements(UUID userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de usuário não encontrado"));

        return achievementRepository.findByUserProfileOrderByEarnedAtDesc(userProfile);
    }

    @Transactional(readOnly = true)
    public List<Achievement> getUserCompletedAchievements(UUID userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de usuário não encontrado"));

        return achievementRepository.findByUserProfileAndIsCompletedTrueOrderByCompletedAtDesc(userProfile);
    }

    @Transactional(readOnly = true)
    public List<Achievement> getUserInProgressAchievements(UUID userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de usuário não encontrado"));

        return achievementRepository.findByUserProfileAndIsCompletedFalseAndProgressGreaterThanOrderByEarnedAtDesc(userProfile, 0);
    }

    @Transactional(readOnly = true)
    public List<Achievement> getUserAchievementsByBadgeType(UUID userId, Badge.BadgeType badgeType) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de usuário não encontrado"));

        return achievementRepository.findByUserProfileAndBadgeTypeOrderByEarnedAtDesc(userProfile, badgeType);
    }

    public Achievement updateProgress(UUID achievementId, Integer progress) {
        log.info("Atualizando progresso da conquista ID: {} para {}", achievementId, progress);

        Achievement achievement = achievementRepository.findById(achievementId)
                .orElseThrow(() -> new IllegalArgumentException("Conquista não encontrada"));

        if (achievement.getIsCompleted()) {
            log.warn("Tentativa de atualizar progresso de conquista já concluída ID: {}", achievementId);
            return achievement;
        }

        achievement.setProgress(progress);

        // Verificar se a conquista foi concluída
        if (progress >= achievement.getMaxProgress()) {
            achievement.setIsCompleted(true);
            achievement.setCompletedAt(LocalDateTime.now());
            log.info("Conquista concluída ID: {}", achievementId);
        }

        Achievement updatedAchievement = achievementRepository.save(achievement);
        log.info("Progresso da conquista atualizado com sucesso ID: {}", achievementId);

        return updatedAchievement;
    }

    public Achievement incrementProgress(UUID achievementId, Integer increment) {
        log.info("Incrementando progresso da conquista ID: {} em {}", achievementId, increment);

        Achievement achievement = achievementRepository.findById(achievementId)
                .orElseThrow(() -> new IllegalArgumentException("Conquista não encontrada"));

        if (achievement.getIsCompleted()) {
            log.warn("Tentativa de incrementar progresso de conquista já concluída ID: {}", achievementId);
            return achievement;
        }

        Integer newProgress = achievement.getProgress() + increment;
        return updateProgress(achievementId, newProgress);
    }

    public Achievement completeAchievement(UUID achievementId) {
        log.info("Completando conquista ID: {}", achievementId);

        Achievement achievement = achievementRepository.findById(achievementId)
                .orElseThrow(() -> new IllegalArgumentException("Conquista não encontrada"));

        if (achievement.getIsCompleted()) {
            log.warn("Conquista já estava concluída ID: {}", achievementId);
            return achievement;
        }

        achievement.setProgress(achievement.getMaxProgress());
        achievement.setIsCompleted(true);
        achievement.setCompletedAt(LocalDateTime.now());

        Achievement completedAchievement = achievementRepository.save(achievement);
        log.info("Conquista concluída com sucesso ID: {}", achievementId);

        return completedAchievement;
    }

    public void deleteAchievement(UUID achievementId) {
        log.info("Deletando conquista ID: {}", achievementId);

        if (!achievementRepository.existsById(achievementId)) {
            throw new IllegalArgumentException("Conquista não encontrada");
        }

        achievementRepository.deleteById(achievementId);
        log.info("Conquista deletada com sucesso ID: {}", achievementId);
    }

    @Transactional(readOnly = true)
    public long countUserCompletedAchievements(UUID userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Perfil de usuário não encontrado"));

        return achievementRepository.countByUserProfileAndIsCompletedTrue(userProfile);
    }

    @Transactional(readOnly = true)
    public long countUserTotalAchievements(UUID userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Perfil de usuário não encontrado"));

        return achievementRepository.countByUserProfile(userProfile);
    }

    @Transactional(readOnly = true)
    public Double getCompletionPercentage(UUID achievementId) {
        Achievement achievement = achievementRepository.findById(achievementId)
                .orElseThrow(() -> new IllegalArgumentException("Conquista não encontrada"));

        if (achievement.getMaxProgress() == 0) {
            return 0.0;
        }

        return (double) achievement.getProgress() / achievement.getMaxProgress() * 100.0;
    }

    // Métodos para verificar e criar conquistas automáticas baseadas em atividades
    public void checkAndCreateTrailAchievements(UUID userId, Integer trailsCompleted) {
        log.info("Verificando conquistas de trilhas para usuário ID: {} com {} trilhas", userId, trailsCompleted);

        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de usuário não encontrado"));

        // Buscar insígnias de trilhas disponíveis
        List<Badge> trailBadges = badgeRepository.findByTypeAndIsActiveTrueOrderByCreatedAtAsc(Badge.BadgeType.TRAIL);

        for (Badge badge : trailBadges) {
            // Verificar se usuário já tem esta conquista
            if (!achievementRepository.existsByUserProfileAndBadge(userProfile, badge)) {
                // Verificar se atende aos critérios
                if (trailsCompleted >= badge.getPointsRequired()) {
                    Achievement achievement = createAchievement(
                        userId, 
                        badge.getId(), 
                        "Conquista automática por completar " + trailsCompleted + " trilhas",
                        badge.getMaxProgress(),
                        Map.of("auto_created", true, "trails_completed", trailsCompleted)
                    );
                    completeAchievement(achievement.getId());
                }
            }
        }
    }

    public void checkAndCreateDistanceAchievements(UUID userId, Double totalDistance) {
        log.info("Verificando conquistas de distância para usuário ID: {} com {} km", userId, totalDistance);

        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de usuário não encontrado"));

        // Buscar insígnias de distância disponíveis
        List<Badge> distanceBadges = badgeRepository.findByTypeAndIsActiveTrueOrderByCreatedAtAsc(Badge.BadgeType.DISTANCE);

        for (Badge badge : distanceBadges) {
            if (!achievementRepository.existsByUserProfileAndBadge(userProfile, badge)) {
                if (totalDistance >= badge.getPointsRequired()) {
                    Achievement achievement = createAchievement(
                        userId, 
                        badge.getId(), 
                        "Conquista automática por percorrer " + totalDistance + " km",
                        badge.getMaxProgress(),
                        Map.of("auto_created", true, "total_distance", totalDistance)
                    );
                    completeAchievement(achievement.getId());
                }
            }
        }
    }
}