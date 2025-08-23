package com.namata.userprofile.service;

import com.namata.userprofile.entity.Badge;
import com.namata.userprofile.repository.BadgeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BadgeService {

    private final BadgeRepository badgeRepository;

    public Badge createBadge(String name, String description, String iconUrl, 
                           Badge.BadgeType type, Badge.Rarity rarity, 
                           Integer pointsRequired, Integer maxProgress, 
                           String criteria) {
        log.info("Criando insígnia: {}", name);

        // Verificar se já existe insígnia com este nome
        if (badgeRepository.existsByName(name)) {
            throw new IllegalArgumentException("Insígnia com este nome já existe");
        }

        Badge badge = Badge.builder()
                .name(name)
                .description(description)
                .iconUrl(iconUrl)
                .type(type)
                .rarity(rarity)
                .pointsRequired(pointsRequired != null ? pointsRequired : 0)
                .maxProgress(maxProgress != null ? maxProgress : 1)
                .criteria(criteria)
                .isActive(true)
                .build();

        Badge savedBadge = badgeRepository.save(badge);
        log.info("Insígnia criada com sucesso ID: {}", savedBadge.getId());

        return savedBadge;
    }

    @Transactional(readOnly = true)
    public Optional<Badge> getBadgeById(UUID badgeId) {
        return badgeRepository.findById(badgeId);
    }

    @Transactional(readOnly = true)
    public Optional<Badge> getBadgeByName(String name) {
        return badgeRepository.findByName(name);
    }

    @Transactional(readOnly = true)
    public List<Badge> getAllActiveBadges() {
        return badgeRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<Badge> getBadgesByType(Badge.BadgeType type) {
        return badgeRepository.findByTypeAndIsActiveTrueOrderByCreatedAtDesc(type);
    }

    @Transactional(readOnly = true)
    public List<Badge> getBadgesByRarity(Badge.Rarity rarity) {
        return badgeRepository.findByRarityAndIsActiveTrueOrderByCreatedAtDesc(rarity);
    }

    @Transactional(readOnly = true)
    public List<Badge> getBadgesByTypeAndRarity(Badge.BadgeType type, Badge.Rarity rarity) {
        return badgeRepository.findByTypeAndRarityAndIsActiveTrueOrderByCreatedAtDesc(type, rarity);
    }

    @Transactional(readOnly = true)
    public List<Badge> getAvailableBadgesByPoints(Integer points) {
        return badgeRepository.findByPointsRequiredLessThanEqualAndIsActiveTrueOrderByPointsRequiredAsc(points);
    }

    public Badge updateBadge(UUID badgeId, String name, String description, String iconUrl,
                           Badge.BadgeType type, Badge.Rarity rarity, 
                           Integer pointsRequired, Integer maxProgress, 
                           String criteria) {
        log.info("Atualizando insígnia ID: {}", badgeId);

        Badge badge = badgeRepository.findById(badgeId)
                .orElseThrow(() -> new IllegalArgumentException("Insígnia não encontrada"));

        // Verificar se o novo nome já existe (se foi alterado)
        if (name != null && !name.equals(badge.getName()) && badgeRepository.existsByName(name)) {
            throw new IllegalArgumentException("Insígnia com este nome já existe");
        }

        // Atualizar apenas campos não nulos
        if (name != null) {
            badge.setName(name);
        }
        if (description != null) {
            badge.setDescription(description);
        }
        if (iconUrl != null) {
            badge.setIconUrl(iconUrl);
        }
        if (type != null) {
            badge.setType(type);
        }
        if (rarity != null) {
            badge.setRarity(rarity);
        }
        if (pointsRequired != null) {
            badge.setPointsRequired(pointsRequired);
        }
        if (maxProgress != null) {
            badge.setMaxProgress(maxProgress);
        }
        if (criteria != null) {
            badge.setCriteria(criteria);
        }

        Badge updatedBadge = badgeRepository.save(badge);
        log.info("Insígnia atualizada com sucesso ID: {}", badgeId);

        return updatedBadge;
    }

    public Badge deactivateBadge(UUID badgeId) {
        log.info("Desativando insígnia ID: {}", badgeId);

        Badge badge = badgeRepository.findById(badgeId)
                .orElseThrow(() -> new IllegalArgumentException("Insígnia não encontrada"));

        badge.setIsActive(false);
        Badge updatedBadge = badgeRepository.save(badge);

        log.info("Insígnia desativada com sucesso ID: {}", badgeId);
        return updatedBadge;
    }

    public Badge activateBadge(UUID badgeId) {
        log.info("Ativando insígnia ID: {}", badgeId);

        Badge badge = badgeRepository.findById(badgeId)
                .orElseThrow(() -> new IllegalArgumentException("Insígnia não encontrada"));

        badge.setIsActive(true);
        Badge updatedBadge = badgeRepository.save(badge);

        log.info("Insígnia ativada com sucesso ID: {}", badgeId);
        return updatedBadge;
    }

    @Transactional(readOnly = true)
    public long countActiveBadges() {
        return badgeRepository.countActiveBadges();
    }

    @Transactional(readOnly = true)
    public long countBadgesByType(Badge.BadgeType type) {
        return badgeRepository.countByTypeAndIsActiveTrue(type);
    }

    // Métodos para criar insígnias padrão do sistema
    public void createDefaultBadges() {
        log.info("Criando insígnias padrão do sistema");

        // Insígnias de Trilhas
        createTrailBadges();
        
        // Insígnias de Distância
        createDistanceBadges();
        
        // Insígnias de Elevação
        createElevationBadges();
        
        // Insígnias Sociais
        createSocialBadges();
        
        // Insígnias Especiais
        createSpecialBadges();

        log.info("Insígnias padrão criadas com sucesso");
    }

    private void createTrailBadges() {
        // Primeira Trilha
        if (!badgeRepository.existsByName("Primeira Trilha")) {
            createBadge("Primeira Trilha", "Complete sua primeira trilha", 
                       "/icons/first-trail.svg", Badge.BadgeType.TRAIL, Badge.Rarity.COMMON,
                       1, 1, "{\"trails_required\": 1}");
        }

        // Explorador
        if (!badgeRepository.existsByName("Explorador")) {
            createBadge("Explorador", "Complete 10 trilhas", 
                       "/icons/explorer.svg", Badge.BadgeType.TRAIL, Badge.Rarity.COMMON,
                       10, 1, "{\"trails_required\": 10}");
        }

        // Aventureiro
        if (!badgeRepository.existsByName("Aventureiro")) {
            createBadge("Aventureiro", "Complete 50 trilhas", 
                       "/icons/adventurer.svg", Badge.BadgeType.TRAIL, Badge.Rarity.RARE,
                       50, 1, "{\"trails_required\": 50}");
        }

        // Mestre das Trilhas
        if (!badgeRepository.existsByName("Mestre das Trilhas")) {
            createBadge("Mestre das Trilhas", "Complete 100 trilhas", 
                       "/icons/trail-master.svg", Badge.BadgeType.TRAIL, Badge.Rarity.EPIC,
                       100, 1, "{\"trails_required\": 100}");
        }
    }

    private void createDistanceBadges() {
        // Caminhante
        if (!badgeRepository.existsByName("Caminhante")) {
            createBadge("Caminhante", "Percorra 10 km em trilhas", 
                       "/icons/walker.svg", Badge.BadgeType.DISTANCE, Badge.Rarity.COMMON,
                       10, 1, "{\"distance_required\": 10.0}");
        }

        // Maratonista
        if (!badgeRepository.existsByName("Maratonista")) {
            createBadge("Maratonista", "Percorra 100 km em trilhas", 
                       "/icons/marathoner.svg", Badge.BadgeType.DISTANCE, Badge.Rarity.RARE,
                       100, 1, "{\"distance_required\": 100.0}");
        }

        // Ultra Maratonista
        if (!badgeRepository.existsByName("Ultra Maratonista")) {
            createBadge("Ultra Maratonista", "Percorra 500 km em trilhas", 
                       "/icons/ultra-marathoner.svg", Badge.BadgeType.DISTANCE, Badge.Rarity.EPIC,
                       500, 1, "{\"distance_required\": 500.0}");
        }
    }

    private void createElevationBadges() {
        // Escalador
        if (!badgeRepository.existsByName("Escalador")) {
            createBadge("Escalador", "Ganhe 1000m de elevação", 
                       "/icons/climber.svg", Badge.BadgeType.ELEVATION, Badge.Rarity.COMMON,
                       1000, 1, "{\"elevation_required\": 1000}");
        }

        // Montanhista
        if (!badgeRepository.existsByName("Montanhista")) {
            createBadge("Montanhista", "Ganhe 5000m de elevação", 
                       "/icons/mountaineer.svg", Badge.BadgeType.ELEVATION, Badge.Rarity.RARE,
                       5000, 1, "{\"elevation_required\": 5000}");
        }
    }

    private void createSocialBadges() {
        // Fotógrafo
        if (!badgeRepository.existsByName("Fotógrafo")) {
            createBadge("Fotógrafo", "Compartilhe 10 fotos", 
                       "/icons/photographer.svg", Badge.BadgeType.SOCIAL, Badge.Rarity.COMMON,
                       10, 1, "{\"photos_required\": 10}");
        }

        // Influenciador
        if (!badgeRepository.existsByName("Influenciador")) {
            createBadge("Influenciador", "Receba 100 curtidas", 
                       "/icons/influencer.svg", Badge.BadgeType.SOCIAL, Badge.Rarity.RARE,
                       100, 1, "{\"likes_required\": 100}");
        }
    }

    private void createSpecialBadges() {
        // Pioneiro
        if (!badgeRepository.existsByName("Pioneiro")) {
            createBadge("Pioneiro", "Um dos primeiros usuários da plataforma", 
                       "/icons/pioneer.svg", Badge.BadgeType.SPECIAL, Badge.Rarity.LEGENDARY,
                       0, 1, "{\"special\": \"early_adopter\"}");
        }

        // Verificado
        if (!badgeRepository.existsByName("Verificado")) {
            createBadge("Verificado", "Perfil verificado pela equipe", 
                       "/icons/verified.svg", Badge.BadgeType.SPECIAL, Badge.Rarity.RARE,
                       0, 1, "{\"special\": \"verified_profile\"}");
        }
    }
}