package org.project.alakazam.projectalakazam.controller;

import org.project.alakazam.projectalakazam.domain.IndexSuggestion;
import org.project.alakazam.projectalakazam.domain.QueryHistory;
import org.project.alakazam.projectalakazam.repository.IndexSuggestionRepository;
import org.project.alakazam.projectalakazam.repository.QueryHistoryRepository;
import org.project.alakazam.projectalakazam.service.QueryExecutionService;
import org.project.alakazam.projectalakazam.service.QueryPerformancePredictor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.project.alakazam.projectalakazam.service.DatabaseSchemaAnalyzer;
import org.project.alakazam.projectalakazam.service.IndexSuggestionEngine;
import org.project.alakazam.projectalakazam.service.NLPService;

@Controller
public class QueryController {

    private final QueryHistoryRepository queryHistoryRepository;
    private final DatabaseSchemaAnalyzer databaseSchemaAnalyzer;
    private final NLPService nlpService;
    private final QueryExecutionService queryExecutionService;
    private final IndexSuggestionRepository indexSuggestionRepository;
    private final IndexSuggestionEngine indexSuggestionEngine;
    private final QueryPerformancePredictor queryPerformancePredictor;

    // Use constructor injection for all dependencies
    @Autowired
    public QueryController(QueryHistoryRepository queryHistoryRepository,
                           DatabaseSchemaAnalyzer databaseSchemaAnalyzer,
                           NLPService nlpService, QueryExecutionService queryExecutionService,
                           IndexSuggestionRepository indexSuggestionRepository,
                           IndexSuggestionEngine indexSuggestionEngine,
                           QueryPerformancePredictor queryPerformancePredictor) {
        this.queryHistoryRepository = queryHistoryRepository;
        this.databaseSchemaAnalyzer = databaseSchemaAnalyzer;
        this.nlpService = nlpService;
        this.queryExecutionService = queryExecutionService;
        this.indexSuggestionRepository = indexSuggestionRepository;
        this.indexSuggestionEngine = indexSuggestionEngine;
        this.queryPerformancePredictor = queryPerformancePredictor;
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
        QueryHistory newEntry = new QueryHistory();
        newEntry.setNaturalLanguageQuery(naturalLanguageQuery);

        if (schemaOptional.isEmpty()) {
            String errorMessage = "ERROR: There are no tables in the database to query, or I could not read the schema.";
            newEntry.setGeneratedSql(errorMessage);
            return queryHistoryRepository.save(newEntry);
        }

        String schema = schemaOptional.get();
        String generatedSql = nlpService.convertToSQL(naturalLanguageQuery, schema);
        newEntry.setGeneratedSql(generatedSql);

        long executionTime = -1;
        String prediction = "N/A";

        if (!generatedSql.toUpperCase().startsWith("ERROR:")) {
            prediction = queryPerformancePredictor.predictPerformance(generatedSql);
            executionTime = queryExecutionService.executeAndMeasure(generatedSql);
        }

        newEntry.setPredictedPerformance(prediction);
        newEntry.setExecutionTimeMs((int) executionTime);

        return queryHistoryRepository.save(newEntry);
    }

    @MutationMapping
    public String explainQuery(@Argument String sql) {
        return nlpService.explainSQL(sql);
    }


}
