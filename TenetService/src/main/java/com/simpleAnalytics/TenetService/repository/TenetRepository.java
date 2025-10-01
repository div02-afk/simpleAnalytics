package com.simpleAnalytics.TenetService.repository;

import com.simpleAnalytics.TenetService.entity.Plan;
import com.simpleAnalytics.TenetService.entity.Tenet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface TenetRepository extends JpaRepository<Tenet,UUID> {


    @Query
    Optional<Plan> findPlanById(UUID id);

    @Query("SELECT t.plan.monthlyCreditLimit FROM Tenet t WHERE t.id = :id")
    Optional<Long> findPlanCreditLimitById(@Param("id") UUID id);

}
