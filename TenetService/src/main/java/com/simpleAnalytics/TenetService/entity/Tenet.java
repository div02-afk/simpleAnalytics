package com.simpleAnalytics.TenetService.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class Tenet {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    private String name;

    // One tenet has many applications
    @OneToMany(mappedBy = "tenet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("tenet-applications")
    private List<Application> applicationsList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    private Timestamp createdAt;
}
