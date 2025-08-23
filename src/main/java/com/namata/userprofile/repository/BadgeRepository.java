package com.namata.userprofile.repository;

import com.namata.userprofile.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, UUID> {

    Optional<Badge> findByName(String name);

    List<Badge> findByIsActiveTrueOrderByCreatedAtDesc();

    List<Badge> findByTypeAndIsActiveTrueOrderByCreatedAtDesc(Badge.BadgeType type);

    List<Badge> findByTypeAndIsActiveTrueOrderByCreatedAtAsc(Badge.BadgeType type);

    List<Badge> findByRarityAndIsActiveTrueOrderByCreatedAtDesc(Badge.Rarity rarity);

    @Query("SELECT b FROM Badge b WHERE b.type = :type AND b.rarity = :rarity AND b.isActive = true ORDER BY b.createdAt DESC")
    List<Badge> findByTypeAndRarityAndIsActiveTrueOrderByCreatedAtDesc(
            @Param("type") Badge.BadgeType type, 
            @Param("rarity") Badge.Rarity rarity
    );

    @Query("SELECT b FROM Badge b WHERE b.pointsRequired <= :points AND b.isActive = true ORDER BY b.pointsRequired DESC")
    List<Badge> findAvailableBadgesForPoints(@Param("points") Integer points);

    @Query("SELECT COUNT(b) FROM Badge b WHERE b.isActive = true")
    long countActiveBadges();

    @Query("SELECT COUNT(b) FROM Badge b WHERE b.type = :type AND b.isActive = true")
    long countByTypeAndIsActiveTrue(@Param("type") Badge.BadgeType type);
    
    @Query("SELECT b FROM Badge b WHERE b.pointsRequired <= :points AND b.isActive = true ORDER BY b.pointsRequired ASC")
    List<Badge> findByPointsRequiredLessThanEqualAndIsActiveTrueOrderByPointsRequiredAsc(@Param("points") Integer points);
    
    @Query("SELECT COUNT(b) FROM Badge b WHERE b.isActive = true")
    long countByIsActiveTrue();

    boolean existsByName(String name);
}