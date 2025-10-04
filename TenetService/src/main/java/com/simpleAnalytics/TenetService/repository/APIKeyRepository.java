package com.simpleAnalytics.TenetService.repository;

import com.simpleAnalytics.TenetService.entity.APIKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface APIKeyRepository extends JpaRepository<APIKey,UUID> {

    @Query("SELECT ak.application.id FROM APIKey ak WHERE ak.id = :apiKey")
    Optional<UUID> findApplicationIdById(UUID apiKey);
}
