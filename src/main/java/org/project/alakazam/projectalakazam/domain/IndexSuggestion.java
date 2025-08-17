package org.project.alakazam.projectalakazam.domain;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "index_suggestions")
public class IndexSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "table_name", nullable = false)
    private String tableName;

    @Type(ListArrayType.class)
    @Column(name = "column_names", nullable = false, columnDefinition = "text[]")
    private List<String> columnNames;

    @Column(name = "suggestion_reason")
    private String suggestionReason;

    @Column(name = "impact_score")
    private Double impactScore;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

}
