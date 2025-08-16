package org.project.alakazam.projectalakazam.controller;

import org.project.alakazam.projectalakazam.domain.QueryHistory;
import org.project.alakazam.projectalakazam.repository.QueryHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

import org.project.alakazam.projectalakazam.service.DatabaseSchemaAnalyzer;
import org.project.alakazam.projectalakazam.service.NLPService;

@Controller
public class QueryController {

    private final QueryHistoryRepository queryHistoryRepository;
    private final DatabaseSchemaAnalyzer databaseSchemaAnalyzer;
    private final NLPService nlpService;

    // Use constructor injection for all dependencies
    @Autowired
    public QueryController(QueryHistoryRepository queryHistoryRepository,
                           DatabaseSchemaAnalyzer databaseSchemaAnalyzer,
                           NLPService nlpService) {
        this.queryHistoryRepository = queryHistoryRepository;
        this.databaseSchemaAnalyzer = databaseSchemaAnalyzer;
        this.nlpService = nlpService;
    }


    @QueryMapping
    public List<QueryHistory> allQueryHistory() {
        return queryHistoryRepository.findAll();
    }

    @MutationMapping
    public QueryHistory submitQuery(@Argument String naturalLanguageQuery) {

        // 1. Analyze the database to get an Optional schema string.
        Optional<String> schemaOptional = databaseSchemaAnalyzer.getSchemaAsCreateTableStatements();

        // 2. The Guardrail: Check if the schema is present.
        if (schemaOptional.isEmpty()) {
            String errorMessage = "ERROR: There are no tables in the database to query, or I could not read the schema.";

            // We still save this attempt so the user can see the feedback
            QueryHistory errorEntry = new QueryHistory();
            errorEntry.setNaturalLanguageQuery(naturalLanguageQuery);
            errorEntry.setGeneratedSql(errorMessage);
            return queryHistoryRepository.save(errorEntry);
        }

        // 3. If the schema exists, proceed with the AI call.
        String schema = schemaOptional.get();
        String generatedSql = nlpService.convertToSQL(naturalLanguageQuery, schema);

        // 4. Create and save the history entry with the REAL generated SQL or AI-generated error.
        QueryHistory newEntry = new QueryHistory();
        newEntry.setNaturalLanguageQuery(naturalLanguageQuery);
        newEntry.setGeneratedSql(generatedSql);

        return queryHistoryRepository.save(newEntry);
    }
}
