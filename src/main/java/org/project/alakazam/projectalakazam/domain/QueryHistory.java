package org.project.alakazam.projectalakazam.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "query_history")
public class QueryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String naturalLanguageQuery;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String generatedSql;

    @Column(name = "execution_time_ms")
    private Integer executionTimeMs;

    @Column(name = "predicted_performance")
    private String predictedPerformance;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

}