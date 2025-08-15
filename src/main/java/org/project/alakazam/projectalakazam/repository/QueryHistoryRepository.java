package org.project.alakazam.projectalakazam.repository;


import org.project.alakazam.projectalakazam.domain.QueryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QueryHistoryRepository extends JpaRepository<QueryHistory, UUID> {

}
