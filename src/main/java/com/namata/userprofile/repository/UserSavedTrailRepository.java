package com.namata.userprofile.repository;

import com.namata.userprofile.entity.UserProfile;
import com.namata.userprofile.entity.UserSavedTrail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSavedTrailRepository extends JpaRepository<UserSavedTrail, UUID> {

    List<UserSavedTrail> findByUserProfileAndIsActiveTrueOrderBySavedAtDesc(UserProfile userProfile);

    Page<UserSavedTrail> findByUserProfileAndIsActiveTrueOrderBySavedAtDesc(UserProfile userProfile, Pageable pageable);

    Optional<UserSavedTrail> findByUserProfileAndTrailIdAndIsActiveTrue(UserProfile userProfile, UUID trailId);

    boolean existsByUserProfileAndTrailIdAndIsActiveTrue(UserProfile userProfile, UUID trailId);

    Optional<UserSavedTrail> findByUserProfileAndTrailId(UserProfile userProfile, UUID trailId);

    @Query("SELECT COUNT(ust) FROM UserSavedTrail ust WHERE ust.userProfile = :userProfile AND ust.isActive = true")
    long countByUserProfileAndIsActiveTrue(@Param("userProfile") UserProfile userProfile);

    @Query("SELECT ust.trailId FROM UserSavedTrail ust WHERE ust.userProfile = :userProfile AND ust.isActive = true ORDER BY ust.savedAt DESC")
    List<UUID> findTrailIdsByUserProfileAndIsActiveTrueOrderBySavedAtDesc(@Param("userProfile") UserProfile userProfile);
}