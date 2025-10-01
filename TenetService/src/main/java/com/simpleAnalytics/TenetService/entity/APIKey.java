package com.simpleAnalytics.TenetService.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class APIKey {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    private String name;

    private Timestamp createdAt;

    // Many API keys belong to one application
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    @JsonBackReference("application-apikeys")
    private Application application;
}
