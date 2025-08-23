package com.namata.userprofile.repository;

import com.namata.userprofile.entity.Achievement;
import com.namata.userprofile.entity.Badge;
import com.namata.userprofile.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, UUID> {

    List<Achievement> findByUserProfileOrderByEarnedAtDesc(UserProfile userProfile);

    List<Achievement> findByUserProfileAndIsCompletedTrueOrderByCompletedAtDesc(UserProfile userProfile);

    List<Achievement> findByUserProfileAndIsCompletedFalseOrderByEarnedAtDesc(UserProfile userProfile);

    Optional<Achievement> findByUserProfileAndBadge(UserProfile userProfile, Badge badge);

    long countByUserProfileAndIsCompletedTrue(UserProfile userProfile);

    List<Achievement> findByUserProfileAndIsCompletedFalseAndProgressGreaterThanOrderByEarnedAtDesc(UserProfile userProfile, Integer progress);

    @Query("SELECT a FROM Achievement a WHERE a.userProfile = :userProfile AND a.badge.type = :badgeType ORDER BY a.earnedAt DESC")
    List<Achievement> findByUserProfileAndBadgeType(@Param("userProfile") UserProfile userProfile, @Param("badgeType") Badge.BadgeType badgeType);

    List<Achievement> findByUserProfileAndBadgeTypeOrderByEarnedAtDesc(UserProfile userProfile, Badge.BadgeType badgeType);

    @Query("SELECT COUNT(a) FROM Achievement a WHERE a.userProfile = :userProfile AND a.isCompleted = true")
    long countCompletedByUserProfile(@Param("userProfile") UserProfile userProfile);

    @Query("SELECT COUNT(a) FROM Achievement a WHERE a.userProfile = :userProfile")
    long countByUserProfile(@Param("userProfile") UserProfile userProfile);

    @Query("SELECT a FROM Achievement a WHERE a.userProfile = :userProfile AND a.progress < 100 ORDER BY a.progress DESC")
    List<Achievement> findInProgressByUserProfile(@Param("userProfile") UserProfile userProfile);

    @Query("SELECT a FROM Achievement a WHERE a.badge = :badge ORDER BY a.completedAt DESC")
    List<Achievement> findByBadgeOrderByCompletedAtDesc(@Param("badge") Badge badge);

    boolean existsByUserProfileAndBadge(UserProfile userProfile, Badge badge);
}