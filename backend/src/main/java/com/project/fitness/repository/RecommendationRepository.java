package com.project.fitness.repository;

import com.project.fitness.model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, String> {
    List<Recommendation> getByUserId(String userId);
    @Query("select recommendation from Recommendation recommendation join fetch recommendation.activity where recommendation.user.id = :userId order by recommendation.createdAt desc")
    List<Recommendation> findByUserIdOrderByCreatedAtDesc(@Param("userId") String userId);

    List<Recommendation> getByActivityId(String activityId);
}
