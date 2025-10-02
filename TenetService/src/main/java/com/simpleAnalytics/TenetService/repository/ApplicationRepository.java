package com.simpleAnalytics.TenetService.repository;

import com.simpleAnalytics.TenetService.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {

    @Modifying
    @Query("UPDATE Application u SET u.creditsUsed = u.creditsUsed + :credits WHERE u.id = :id")
    void incrementCredits(UUID id, long credits);


    @Modifying
    @Query("UPDATE Application u SET u.creditsUsed = 0")
    void resetCredits();
}
