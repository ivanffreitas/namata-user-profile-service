package com.namata.userprofile.service;

import com.namata.userprofile.dto.StatisticsDTO;
import com.namata.userprofile.dto.FormattedStatisticsDTO;
import com.namata.userprofile.entity.Statistics;
import com.namata.userprofile.entity.UserProfile;
import com.namata.userprofile.repository.StatisticsRepository;
import com.namata.userprofile.repository.UserProfileRepository;
import com.namata.userprofile.util.StatisticsFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;
    private final UserProfileRepository userProfileRepository;

    @Transactional
    public Statistics createStatistics(UUID userId) {
        log.info("Criando estatísticas para usuário ID: {}", userId);

        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de usuário não encontrado"));

        // Verificar se já existem estatísticas
        if (statisticsRepository.existsByUserProfile(userProfile)) {
            throw new IllegalArgumentException("Estatísticas já existem para este usuário");
        }

        Statistics statistics = Statistics.builder()
                .userProfile(userProfile)
                .build();

        Statistics savedStatistics = statisticsRepository.save(statistics);
        log.info("Estatísticas criadas com sucesso para usuário ID: {}", userId);

        return savedStatistics;
    }

    public Optional<StatisticsDTO> getUserStatistics(UUID userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de usuário não encontrado"));

        return statisticsRepository.findByUserProfile(userProfile)
                .map(this::convertToDTO);
    }

    public Optional<Statistics> getStatisticsById(UUID statisticsId) {
        return statisticsRepository.findById(statisticsId);
    }

    @Transactional
    public StatisticsDTO getStatisticsByUserId(UUID userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de usuário não encontrado"));

        Statistics statistics = statisticsRepository.findByUserProfile(userProfile)
                .orElseGet(() -> createStatistics(userId));

        return convertToDTO(statistics);
    }
    
    @Transactional
    public FormattedStatisticsDTO getFormattedStatisticsByUserId(UUID userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de usuário não encontrado"));

        Statistics statistics = statisticsRepository.findByUserProfile(userProfile)
                .orElseGet(() -> createStatistics(userId));

        return convertToFormattedDTO(statistics);
    }

    // Métodos para atualizar estatísticas de trilhas
    @Transactional
    public StatisticsDTO updateTrailStatistics(UUID userId, Integer trailsCompleted, 
                                          Double totalDistance, Integer totalTime, 
                                          Double totalElevationGain, Double longestTrail, 
                                          Integer highestElevation) {
        return updateTrailStatistics(userId, trailsCompleted, totalDistance, totalTime, 
                                   totalElevationGain, longestTrail, highestElevation, null);
    }
    
    public StatisticsDTO updateTrailStatistics(UUID userId, Integer trailsCompleted, 
                                          Double totalDistance, Integer totalTime, 
                                          Double totalElevationGain, Double longestTrail, 
                                          Integer highestElevation, Integer totalPoints) {
        log.info("Atualizando estatísticas de trilhas para usuário ID: {}", userId);

        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de usuário não encontrado"));

        Statistics statistics = statisticsRepository.findByUserProfile(userProfile)
                .orElseGet(() -> createStatistics(userId));

        if (trailsCompleted != null) {
            statistics.setTotalTrailsCompleted(trailsCompleted);
        }
        if (totalDistance != null) {
            statistics.setTotalDistanceKm(totalDistance);
        }
        if (totalTime != null) {
            statistics.setTotalTimeMinutes(totalTime);
        }
        if (totalElevationGain != null) {
            statistics.setTotalElevationGainM(totalElevationGain);
        }
        if (longestTrail != null) {
            statistics.setLongestTrailKm(longestTrail.intValue());
        }
        if (highestElevation != null) {
            statistics.setHighestElevationM(highestElevation);
        }
        if (totalPoints != null) {
            statistics.setTotalPoints(totalPoints);
        }

        Statistics updatedStatistics = statisticsRepository.save(statistics);
        
        log.info("Estatísticas de trilhas atualizadas para usuário ID: {}", userId);
        StatisticsDTO dto = convertToDTO(updatedStatistics);

        return dto;
    }

    // Métodos para incrementar estatísticas de trilhas
    public Statistics incrementTrailStatistics(UUID userId, Integer trailsIncrement, 
                                             Double distanceIncrement, Integer timeIncrement, 
                                             Integer elevationIncrement) {
        log.info("Incrementando estatísticas de trilhas para usuário ID: {}", userId);

        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de usuário não encontrado"));

        Statistics statistics = statisticsRepository.findByUserProfile(userProfile)
                .orElseGet(() -> createStatistics(userId));

        if (trailsIncrement != null) {
            statistics.setTotalTrailsCompleted(statistics.getTotalTrailsCompleted() + trailsIncrement);
        }
        if (distanceIncrement != null) {
            statistics.setTotalDistanceKm(statistics.getTotalDistanceKm() + distanceIncrement);
        }
        if (timeIncrement != null) {
            statistics.setTotalTimeMinutes(statistics.getTotalTimeMinutes() + timeIncrement);
        }
        if (elevationIncrement != null) {
            statistics.setTotalElevationGainM(statistics.getTotalElevationGainM() + elevationIncrement);
        }

        Statistics updatedStatistics = statisticsRepository.save(statistics);
        log.info("Estatísticas de trilhas incrementadas para usuário ID: {}", userId);

        return updatedStatistics;
    }

    // Métodos para atualizar estatísticas de atividades
    public StatisticsDTO updateActivityStatistics(UUID userId, Integer photosShared, 
                                             Integer reviewsWritten, Integer likesReceived, 
                                             Integer commentsReceived) {
        log.info("Atualizando estatísticas de atividades para usuário ID: {}", userId);

        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de usuário não encontrado"));

        Statistics statistics = statisticsRepository.findByUserProfile(userProfile)
                .orElseGet(() -> createStatistics(userId));

        if (photosShared != null) {
            statistics.setTotalPhotosShared(photosShared);
        }
        if (reviewsWritten != null) {
            statistics.setTotalReviewsPosted(reviewsWritten);
        }
        if (likesReceived != null) {
            statistics.setTotalLikesReceived(likesReceived);
        }
        if (commentsReceived != null) {
            statistics.setTotalCommentsReceived(commentsReceived);
        }

        Statistics updatedStatistics = statisticsRepository.save(statistics);
        log.info("Estatísticas de atividades atualizadas para usuário ID: {}", userId);

        return convertToDTO(updatedStatistics);
    }

    // Métodos para atualizar estatísticas de conquistas
    public StatisticsDTO updateAchievementStatistics(UUID userId, Integer badgesEarned, 
                                                Integer totalPoints, Integer currentStreak, 
                                                Integer longestStreak) {
        log.info("Atualizando estatísticas de conquistas para usuário ID: {}", userId);

        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de usuário não encontrado"));

        Statistics statistics = statisticsRepository.findByUserProfile(userProfile)
                .orElseGet(() -> createStatistics(userId));

        if (badgesEarned != null) {
            statistics.setTotalBadgesEarned(badgesEarned);
        }
        if (totalPoints != null) {
            statistics.setTotalPoints(totalPoints);
        }
        if (currentStreak != null) {
            statistics.setCurrentStreak(currentStreak);
        }
        if (longestStreak != null) {
            statistics.setLongestStreak(longestStreak);
        }

        Statistics updatedStatistics = statisticsRepository.save(statistics);
        log.info("Estatísticas de conquistas atualizadas para usuário ID: {}", userId);

        return convertToDTO(updatedStatistics);
    }

    // Métodos para atualizar estatísticas sociais
    public StatisticsDTO updateSocialStatistics(UUID userId, Integer followers, 
                                           Integer following, Integer guidesBooked) {
        log.info("Atualizando estatísticas sociais para usuário ID: {}", userId);

        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de usuário não encontrado"));

        Statistics statistics = statisticsRepository.findByUserProfile(userProfile)
                .orElseGet(() -> createStatistics(userId));

        if (followers != null) {
            statistics.setTotalFollowers(followers);
        }
        if (following != null) {
            statistics.setTotalFollowing(following);
        }
        if (guidesBooked != null) {
            statistics.setTotalGuidesBooked(guidesBooked);
        }

        Statistics updatedStatistics = statisticsRepository.save(statistics);
        log.info("Estatísticas sociais atualizadas para usuário ID: {}", userId);

        return convertToDTO(updatedStatistics);
    }

    // Métodos para atualizar ranking
    public StatisticsDTO updateRanking(UUID userId, Integer globalRank, Integer localRank) {
        log.info("Atualizando ranking para usuário ID: {}", userId);

        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de usuário não encontrado"));

        Statistics statistics = statisticsRepository.findByUserProfile(userProfile)
                .orElseGet(() -> createStatistics(userId));

        if (globalRank != null) {
            statistics.setGlobalRank(globalRank);
        }
        if (localRank != null) {
            statistics.setLocalRank(localRank);
        }

        Statistics updatedStatistics = statisticsRepository.save(statistics);
        log.info("Ranking atualizado para usuário ID: {}", userId);

        return convertToDTO(updatedStatistics);
    }

    public StatisticsDTO updateLastActivity(UUID userId) {
        log.info("Atualizando última atividade para usuário ID: {}", userId);

        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de usuário não encontrado"));

        Statistics statistics = statisticsRepository.findByUserProfile(userProfile)
                .orElseGet(() -> createStatistics(userId));

        statistics.setLastActivityAt(LocalDateTime.now());

        Statistics updatedStatistics = statisticsRepository.save(statistics);
        log.info("Última atividade atualizada para usuário ID: {}", userId);

        return convertToDTO(updatedStatistics);
    }

    // Métodos para rankings e comparações
    public Page<StatisticsDTO> getTopUsersByPoints(Pageable pageable) {
        List<Statistics> statisticsList = statisticsRepository.findAllOrderByTotalPointsDesc();
        return convertListToPage(statisticsList, pageable);
    }

    public Page<StatisticsDTO> getTopUsersByTrails(Pageable pageable) {
        List<Statistics> statisticsList = statisticsRepository.findAllOrderByTotalTrailsCompletedDesc();
        return convertListToPage(statisticsList, pageable);
    }

    public Page<StatisticsDTO> getTopUsersByDistance(Pageable pageable) {
        List<Statistics> statisticsList = statisticsRepository.findAllOrderByTotalDistanceKmDesc();
        return convertListToPage(statisticsList, pageable);
    }

    public List<StatisticsDTO> getTopUsersByLocation(String location, int limit) {
        return statisticsRepository.findByLocationOrderByTotalPointsDesc(location)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public Double getAveragePoints() {
        return statisticsRepository.getAveragePoints();
    }

    public Double getAverageDistance() {
        return statisticsRepository.getAverageDistance();
    }

    public Double getAverageTrailsCompleted() {
        return statisticsRepository.getAverageTrailsCompleted();
    }

    public Double getMaxDistance() {
        return statisticsRepository.getMaxDistance();
    }

    public Integer getMaxTrailsCompleted() {
        return statisticsRepository.getMaxTrailsCompleted();
    }

    public Integer getMaxPoints() {
        return statisticsRepository.getMaxPoints();
    }

    public Page<StatisticsDTO> getRankingByDistance(Pageable pageable) {
        List<Statistics> statisticsList = statisticsRepository.findAllOrderByTotalDistanceKmDesc();
        return convertListToPage(statisticsList, pageable);
    }

    public List<StatisticsDTO> getRankingByLocation(String location) {
        List<Statistics> statistics = statisticsRepository.findByLocationOrderByTotalPointsDesc(location);
        return statistics.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Page<StatisticsDTO> getRankingByPoints(Pageable pageable) {
        List<Statistics> statisticsList = statisticsRepository.findAllOrderByTotalPointsDesc();
        return convertListToPage(statisticsList, pageable);
    }

    public Page<StatisticsDTO> getRankingByTrails(Pageable pageable) {
        List<Statistics> statisticsList = statisticsRepository.findAllOrderByTotalTrailsCompletedDesc();
        return convertListToPage(statisticsList, pageable);
    }

    @Transactional
    public StatisticsDTO incrementTrailsCompleted(UUID userId, Integer increment) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de usuário não encontrado"));

        Statistics statistics = statisticsRepository.findByUserProfile(userProfile)
                .orElseGet(() -> createStatistics(userId));
        
        statistics.setTotalTrailsCompleted(statistics.getTotalTrailsCompleted() + increment);
        statistics.setUpdatedAt(LocalDateTime.now());
        Statistics saved = statisticsRepository.save(statistics);
        return convertToDTO(saved);
    }

    private Page<StatisticsDTO> convertListToPage(List<Statistics> statisticsList, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), statisticsList.size());
        
        List<StatisticsDTO> pageContent = statisticsList.subList(start, end)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageImpl<>(pageContent, pageable, statisticsList.size());
    }

    public StatisticsDTO convertToDTO(Statistics statistics) {
        return StatisticsDTO.builder()
                .id(statistics.getId())
                .userProfileId(statistics.getUserProfile().getId())
                .totalTrailsCompleted(statistics.getTotalTrailsCompleted())
                .totalDistanceKm(statistics.getTotalDistanceKm())
                .totalTimeMinutes(statistics.getTotalTimeMinutes())
                .totalElevationGainM(statistics.getTotalElevationGainM())
                .longestTrailKm(statistics.getLongestTrailKm())
                .highestElevationM(statistics.getHighestElevationM())
                .totalPhotosShared(statistics.getTotalPhotosShared())
                .totalReviewsPosted(statistics.getTotalReviewsPosted())
                .totalLikesReceived(statistics.getTotalLikesReceived())
                .totalCommentsReceived(statistics.getTotalCommentsReceived())
                .totalBadgesEarned(statistics.getTotalBadgesEarned())
                .totalPoints(statistics.getTotalPoints())
                .currentStreak(statistics.getCurrentStreak())
                .longestStreak(statistics.getLongestStreak())
                .totalFollowers(statistics.getTotalFollowers())
                .totalFollowing(statistics.getTotalFollowing())
                .totalGuidesBooked(statistics.getTotalGuidesBooked())
                .globalRank(statistics.getGlobalRank())
                .localRank(statistics.getLocalRank())
                .lastActivityAt(statistics.getLastActivityAt())
                .updatedAt(statistics.getUpdatedAt())
                .build();
    }
    
    /**
     * Converte Statistics para FormattedStatisticsDTO com formatação para apresentação
     * @param statistics Entidade Statistics
     * @return FormattedStatisticsDTO com valores formatados
     */
    public FormattedStatisticsDTO convertToFormattedDTO(Statistics statistics) {
        return FormattedStatisticsDTO.builder()
                .id(statistics.getId())
                .userProfileId(statistics.getUserProfile().getId())
                .totalTrailsCompleted(statistics.getTotalTrailsCompleted())
                
                // Valores formatados para apresentação
                .totalDistanceFormatted(StatisticsFormatter.formatDistance(statistics.getTotalDistanceKm()))
                .totalTimeFormatted(StatisticsFormatter.formatTime(statistics.getTotalTimeMinutes()))
                .totalElevationGainFormatted(StatisticsFormatter.formatElevation(statistics.getTotalElevationGainM()))
                .longestTrailFormatted(StatisticsFormatter.formatDistance(statistics.getLongestTrailKm().doubleValue()))
                .highestElevationFormatted(StatisticsFormatter.formatElevation(statistics.getHighestElevationM()))
                
                // Valores brutos mantidos para compatibilidade
                .totalDistanceKm(statistics.getTotalDistanceKm())
                .totalTimeMinutes(statistics.getTotalTimeMinutes())
                .totalElevationGainM(statistics.getTotalElevationGainM())
                .longestTrailKm(statistics.getLongestTrailKm())
                .highestElevationM(statistics.getHighestElevationM())
                
                // Outras estatísticas
                .totalPhotosShared(statistics.getTotalPhotosShared())
                .totalReviewsPosted(statistics.getTotalReviewsPosted())
                .totalLikesReceived(statistics.getTotalLikesReceived())
                .totalCommentsReceived(statistics.getTotalCommentsReceived())
                .totalBadgesEarned(statistics.getTotalBadgesEarned())
                .totalPoints(statistics.getTotalPoints())
                .currentStreak(statistics.getCurrentStreak())
                .longestStreak(statistics.getLongestStreak())
                .totalFollowers(statistics.getTotalFollowers())
                .totalFollowing(statistics.getTotalFollowing())
                .totalGuidesBooked(statistics.getTotalGuidesBooked())
                .globalRank(statistics.getGlobalRank())
                .localRank(statistics.getLocalRank())
                .lastActivityAt(statistics.getLastActivityAt())
                .updatedAt(statistics.getUpdatedAt())
                .build();
    }
}