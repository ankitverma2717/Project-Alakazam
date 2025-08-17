package org.project.alakazam.projectalakazam.repository;

import org.project.alakazam.projectalakazam.domain.IndexSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IndexSuggestionRepository extends JpaRepository<IndexSuggestion, UUID> {
}
