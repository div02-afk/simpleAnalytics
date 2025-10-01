package com.simpleAnalytics.TenetService.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Application {

    @Id
    private UUID id;

    private String name;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("application-apikeys")
    private List<APIKey> apiKeysList;

    private String source;

    private Timestamp createdAt;

    private long creditsUsed;

    // Many applications belong to one tenet
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenet_id", nullable = false)
    @JsonBackReference("tenet-applications")
    private Tenet tenet;

    // Helper method to get tenet ID
    public UUID getTenetId() {
        return tenet != null ? tenet.getId() : null;
    }
}
