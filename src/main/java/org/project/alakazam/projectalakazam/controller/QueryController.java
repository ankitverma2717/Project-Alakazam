package org.project.alakazam.projectalakazam.controller;

import org.project.alakazam.projectalakazam.domain.QueryHistory;
import org.project.alakazam.projectalakazam.repository.QueryHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class QueryController {

    @Autowired
    private QueryHistoryRepository queryHistoryRepository;

    // This method handles the "allQueryHistory" query defined in our schema
    @QueryMapping
    public List<QueryHistory> allQueryHistory() {
        return queryHistoryRepository.findAll();
    }

    // This method handles the "submitQuery" mutation
    @MutationMapping
    public QueryHistory submitQuery(@Argument String naturalLanguageQuery) {
        // For now, the NLP logic is just a placeholder
        String generatedSql = "SELECT * FROM users WHERE name = 'a_placeholder';";

        QueryHistory newEntry = new QueryHistory();
        newEntry.setNaturalLanguageQuery(naturalLanguageQuery);
        newEntry.setGeneratedSql(generatedSql);

        return queryHistoryRepository.save(newEntry);
    }
}
