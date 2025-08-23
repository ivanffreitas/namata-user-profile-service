package com.namata.userprofile.repository;

import com.namata.userprofile.entity.Activity;
import com.namata.userprofile.entity.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, UUID> {

    List<Activity> findByUserProfileOrderByCreatedAtDesc(UserProfile userProfile);

    Page<Activity> findByUserProfileOrderByCreatedAtDesc(UserProfile userProfile, Pageable pageable);

    List<Activity> findByUserProfileAndTypeOrderByCreatedAtDesc(UserProfile userProfile, Activity.ActivityType type);

    List<Activity> findByUserProfileAndType(UserProfile userProfile, Activity.ActivityType type);

    List<Activity> findByIsPublicTrueOrderByCreatedAtDesc();

    @Query("SELECT a FROM Activity a WHERE a.userProfile = :userProfile AND a.isPublic = true ORDER BY a.createdAt DESC")
    List<Activity> findPublicActivitiesByUserProfile(@Param("userProfile") UserProfile userProfile);

    @Query("SELECT a FROM Activity a WHERE a.userProfile = :userProfile AND a.createdAt >= :startDate ORDER BY a.createdAt DESC")
    List<Activity> findRecentActivities(@Param("userProfile") UserProfile userProfile, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT a FROM Activity a WHERE a.isPublic = true ORDER BY a.createdAt DESC")
    Page<Activity> findPublicActivities(Pageable pageable);

    List<Activity> findAllByOrderByCreatedAtDesc();

    List<Activity> findByTrailIdOrderByCreatedAtDesc(UUID trailId);

    @Query("SELECT a FROM Activity a WHERE a.type = :type AND a.isPublic = true ORDER BY a.createdAt DESC")
    List<Activity> findByTypeAndIsPublicTrue(@Param("type") Activity.ActivityType type);

    @Query("SELECT COUNT(a) FROM Activity a WHERE a.userProfile = :userProfile")
    long countByUserProfile(@Param("userProfile") UserProfile userProfile);

    @Query("SELECT COUNT(a) FROM Activity a WHERE a.userProfile = :userProfile AND a.type = :type")
    long countByUserProfileAndType(@Param("userProfile") UserProfile userProfile, @Param("type") Activity.ActivityType type);

    @Query("SELECT SUM(a.distance) FROM Activity a WHERE a.userProfile = :userProfile AND a.distance IS NOT NULL")
    Double sumDistanceByUserProfile(@Param("userProfile") UserProfile userProfile);

    @Query("SELECT SUM(a.duration) FROM Activity a WHERE a.userProfile = :userProfile AND a.duration IS NOT NULL")
    Integer sumDurationByUserProfile(@Param("userProfile") UserProfile userProfile);

    @Query("SELECT a FROM Activity a WHERE a.userProfile = :userProfile AND a.trailId = :trailId")
    List<Activity> findByUserProfileAndTrailId(@Param("userProfile") UserProfile userProfile, @Param("trailId") UUID trailId);
}