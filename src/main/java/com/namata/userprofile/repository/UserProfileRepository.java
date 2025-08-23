package com.namata.userprofile.repository;

import com.namata.userprofile.entity.UserProfile;
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
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

    Optional<UserProfile> findByUserId(UUID userId);

    List<UserProfile> findByIsActiveTrue();

    List<UserProfile> findByIsVerifiedTrue();

    @Query("SELECT up FROM UserProfile up WHERE up.isActive = true AND up.isVerified = true")
    List<UserProfile> findActiveVerifiedProfiles();

    @Query("SELECT up FROM UserProfile up WHERE up.location LIKE %:location% AND up.isActive = true")
    List<UserProfile> findByLocationContainingAndIsActiveTrue(@Param("location") String location);

    @Query("SELECT up FROM UserProfile up WHERE up.experienceLevel = :level AND up.isActive = true")
    List<UserProfile> findByExperienceLevelAndIsActiveTrue(@Param("level") UserProfile.ExperienceLevel level);

    @Query("SELECT up FROM UserProfile up WHERE " +
           "(:displayName IS NULL OR LOWER(up.displayName) LIKE LOWER(CONCAT('%', :displayName, '%'))) AND " +
           "(:location IS NULL OR LOWER(up.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:experienceLevel IS NULL OR up.experienceLevel = :experienceLevel) AND " +
           "up.isActive = true")
    Page<UserProfile> findProfilesWithFilters(
            @Param("displayName") String displayName,
            @Param("location") String location,
            @Param("experienceLevel") UserProfile.ExperienceLevel experienceLevel,
            Pageable pageable
    );

    @Query("SELECT COUNT(up) FROM UserProfile up WHERE up.isActive = true")
    long countActiveProfiles();

    @Query("SELECT COUNT(up) FROM UserProfile up WHERE up.isVerified = true")
    long countVerifiedProfiles();

    boolean existsByUserId(UUID userId);
}