package org.project.alakazam.projectalakazam.controller;

import org.project.alakazam.projectalakazam.domain.IndexSuggestion;
import org.project.alakazam.projectalakazam.domain.QueryHistory;
import org.project.alakazam.projectalakazam.repository.IndexSuggestionRepository;
import org.project.alakazam.projectalakazam.repository.QueryHistoryRepository;
import org.project.alakazam.projectalakazam.service.QueryExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.project.alakazam.projectalakazam.service.DatabaseSchemaAnalyzer;
import org.project.alakazam.projectalakazam.service.NLPService;

@Controller
public class QueryController {

    private final QueryHistoryRepository queryHistoryRepository;
    private final DatabaseSchemaAnalyzer databaseSchemaAnalyzer;
    private final NLPService nlpService;
    private final QueryExecutionService queryExecutionService;
    private final IndexSuggestionRepository indexSuggestionRepository;

    // Use constructor injection for all dependencies
    @Autowired
    public QueryController(QueryHistoryRepository queryHistoryRepository,
                           DatabaseSchemaAnalyzer databaseSchemaAnalyzer,
                           NLPService nlpService, QueryExecutionService queryExecutionService,
                           IndexSuggestionRepository indexSuggestionRepository) {
        this.queryHistoryRepository = queryHistoryRepository;
        this.databaseSchemaAnalyzer = databaseSchemaAnalyzer;
        this.nlpService = nlpService;
        this.queryExecutionService = queryExecutionService;
        this.indexSuggestionRepository = indexSuggestionRepository;
    }


    @QueryMapping
    public List<QueryHistory> allQueryHistory() {
        return queryHistoryRepository.findAll();
    }

    @QueryMapping
    public List<IndexSuggestion> allIndexSuggestions() {
        // Fetch suggestions and sort them by impact score in descending order
        return indexSuggestionRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(IndexSuggestion::getImpactScore).reversed())
                .toList();
    }

    @MutationMapping
    public QueryHistory submitQuery(@Argument String naturalLanguageQuery) {
        Optional<String> schemaOptional = databaseSchemaAnalyzer.getSchemaAsCreateTableStatements();

        if (schemaOptional.isEmpty()) {
            // ... (error handling remains the same)
        }

        String schema = schemaOptional.get();
        String generatedSql = nlpService.convertToSQL(naturalLanguageQuery, schema);

        long executionTime = -1;
        // Only try to execute if the AI didn't return an error
        if (!generatedSql.toUpperCase().startsWith("ERROR:")) {
            executionTime = queryExecutionService.executeAndMeasure(generatedSql);
        }

        QueryHistory newEntry = new QueryHistory();
        newEntry.setNaturalLanguageQuery(naturalLanguageQuery);
        newEntry.setGeneratedSql(generatedSql);
        newEntry.setExecutionTimeMs((int) executionTime); // Save the measured time

        return queryHistoryRepository.save(newEntry);
    }

    @MutationMapping
    public String explainQuery(@Argument String sql) {
        return nlpService.explainSQL(sql);
    }
}
