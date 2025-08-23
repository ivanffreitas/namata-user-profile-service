package com.namata.userprofile.repository;

import com.namata.userprofile.entity.Statistics;
import com.namata.userprofile.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StatisticsRepository extends JpaRepository<Statistics, UUID> {

    Optional<Statistics> findByUserProfile(UserProfile userProfile);

    @Query("SELECT s FROM Statistics s ORDER BY s.totalPoints DESC")
    List<Statistics> findAllOrderByTotalPointsDesc();

    @Query("SELECT s FROM Statistics s ORDER BY s.totalTrailsCompleted DESC")
    List<Statistics> findAllOrderByTotalTrailsCompletedDesc();

    @Query("SELECT s FROM Statistics s ORDER BY s.totalDistanceKm DESC")
    List<Statistics> findAllOrderByTotalDistanceKmDesc();

    @Query("SELECT s FROM Statistics s WHERE s.userProfile.location = :location ORDER BY s.totalPoints DESC")
    List<Statistics> findByLocationOrderByTotalPointsDesc(@Param("location") String location);

    @Query("SELECT s FROM Statistics s WHERE s.totalPoints >= :minPoints ORDER BY s.totalPoints DESC")
    List<Statistics> findByTotalPointsGreaterThanEqualOrderByTotalPointsDesc(@Param("minPoints") Integer minPoints);

    @Query("SELECT s FROM Statistics s WHERE s.totalTrailsCompleted >= :minTrails ORDER BY s.totalTrailsCompleted DESC")
    List<Statistics> findByTotalTrailsCompletedGreaterThanEqualOrderByTotalTrailsCompletedDesc(@Param("minTrails") Integer minTrails);

    @Query("SELECT AVG(s.totalPoints) FROM Statistics s")
    Double getAveragePoints();

    @Query("SELECT AVG(s.totalDistanceKm) FROM Statistics s")
    Double getAverageDistance();

    @Query("SELECT AVG(s.totalTrailsCompleted) FROM Statistics s")
    Double getAverageTrailsCompleted();

    @Query("SELECT MAX(s.totalPoints) FROM Statistics s")
    Integer getMaxPoints();

    @Query("SELECT MAX(s.totalDistanceKm) FROM Statistics s")
    Double getMaxDistance();

    @Query("SELECT MAX(s.totalTrailsCompleted) FROM Statistics s")
    Integer getMaxTrailsCompleted();

    boolean existsByUserProfile(UserProfile userProfile);
}