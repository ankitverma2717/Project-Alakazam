package org.project.alakazam.projectalakazam.service;

import org.project.alakazam.projectalakazam.domain.IndexSuggestion;
import org.project.alakazam.projectalakazam.domain.QueryHistory;
import org.project.alakazam.projectalakazam.repository.IndexSuggestionRepository;
import org.project.alakazam.projectalakazam.repository.QueryHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class IndexSuggestionEngine {

    // Regex to find table names and columns within a WHERE clause.
    // It's designed to be simple and capture the most common cases.
    private static final Pattern WHERE_CLAUSE_PATTERN = Pattern.compile(
            "FROM\\s+(\\w+)|JOIN\\s+(\\w+)|WHERE\\s+.*?(\\w+)\\s*[=<>]|AND\\s+.*?(\\w+)\\s*[=<>]|OR\\s+.*?(\\w+)\\s*[=<>]",
            Pattern.CASE_INSENSITIVE
    );

    private final QueryHistoryRepository queryHistoryRepository;
    private final IndexSuggestionRepository indexSuggestionRepository;
    private final DataSource dataSource;

    @Autowired
    public IndexSuggestionEngine(QueryHistoryRepository queryHistoryRepository,
                                 IndexSuggestionRepository indexSuggestionRepository,
                                 DataSource dataSource) {
        this.queryHistoryRepository = queryHistoryRepository;
        this.indexSuggestionRepository = indexSuggestionRepository;
        this.dataSource = dataSource;
    }

    // Runs every 2 minutes for testing, after an initial 30-second delay.
    @Scheduled(fixedRate = 120000, initialDelay = 30000)
    @Transactional
    public void analyzeQueriesAndSuggestIndexes() {
        System.out.println("Starting scheduled query analysis for index suggestions...");

        // 1. Fetch all queries that are successful SELECT statements.
        List<String> queries = queryHistoryRepository.findAll().stream()
                .map(QueryHistory::getGeneratedSql)
                .filter(q -> q != null && q.trim().toUpperCase().startsWith("SELECT"))
                .toList();

        if (queries.isEmpty()) {
            System.out.println("No queries to analyze. Skipping index suggestion.");
            return;
        }

        // 2. Parse queries to find frequently filtered columns.
        Map<String, Long> columnUsageFrequency = parseAndCountColumnUsage(queries);
        System.out.println("Found column usage: " + columnUsageFrequency);

        // 3. Get all existing indexes from the database.
        Set<String> existingIndexes = getExistingIndexes();

        // 4. Generate new, unique suggestions.
        List<IndexSuggestion> newSuggestions = generateSuggestions(columnUsageFrequency, existingIndexes);

        // 5. Save the new suggestions to the database.
        if (!newSuggestions.isEmpty()) {
            indexSuggestionRepository.saveAll(newSuggestions);
            System.out.println("Generated and saved " + newSuggestions.size() + " new index suggestions.");
        }

        System.out.println("Finished query analysis for index suggestions.");
    }

    private Map<String, Long> parseAndCountColumnUsage(List<String> queries) {
        Map<String, Long> frequencyMap = new HashMap<>();
        for (String query : queries) {
            Matcher matcher = WHERE_CLAUSE_PATTERN.matcher(query);
            String currentTable = null;
            while (matcher.find()) {
                if (matcher.group(1) != null) { // FROM clause
                    currentTable = matcher.group(1);
                } else if (matcher.group(2) != null) { // JOIN clause
                    currentTable = matcher.group(2);
                } else if (currentTable != null) { // WHERE/AND/OR clause
                    String column = null;
                    if (matcher.group(3) != null) column = matcher.group(3);
                    else if (matcher.group(4) != null) column = matcher.group(4);
                    else if (matcher.group(5) != null) column = matcher.group(5);

                    if (column != null) {
                        String key = currentTable.toLowerCase() + "." + column.toLowerCase();
                        frequencyMap.put(key, frequencyMap.getOrDefault(key, 0L) + 1);
                    }
                }
            }
        }
        return frequencyMap;
    }

    private Set<String> getExistingIndexes() {
        Set<String> indexes = new HashSet<>();
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    // We only care about user tables, not Flyway's or our own.
                    if (tableName.startsWith("flyway_") || tableName.equals("query_history")) {
                        continue;
                    }
                    try (ResultSet rs = metaData.getIndexInfo(null, null, tableName, false, false)) {
                        while (rs.next()) {
                            String columnName = rs.getString("COLUMN_NAME");
                            if (columnName != null) {
                                indexes.add(tableName.toLowerCase() + "." + columnName.toLowerCase());
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Could not read existing indexes from database: " + e.getMessage());
        }
        return indexes;
    }

    private List<IndexSuggestion> generateSuggestions(Map<String, Long> frequencyMap, Set<String> existingIndexes) {
        List<IndexSuggestion> suggestions = new ArrayList<>();

        // Get suggestions that already exist in our table to avoid duplicates.
        Set<String> alreadySuggested = indexSuggestionRepository.findAll().stream()
                .map(s -> s.getTableName().toLowerCase() + "." + s.getColumnNames().get(0).toLowerCase())
                .collect(Collectors.toSet());

        for (Map.Entry<String, Long> entry : frequencyMap.entrySet()) {
            String tableAndColumn = entry.getKey();
            long usageCount = entry.getValue();

            // Suggest an index if a column is used in a WHERE clause more than 2 times
            // and is not already indexed, and we haven't suggested it before.
            if (usageCount > 10 && !existingIndexes.contains(tableAndColumn) && !alreadySuggested.contains(tableAndColumn)) {
                System.out.println("Creating suggestion for: " + tableAndColumn + " (used " + usageCount + " times)");
                String[] parts = tableAndColumn.split("\\.");
                String tableName = parts[0];
                String columnName = parts[1];

                IndexSuggestion suggestion = new IndexSuggestion();
                suggestion.setTableName(tableName);
                suggestion.setColumnNames(List.of(columnName));
                suggestion.setSuggestionReason("This column is frequently used in WHERE clauses (" + usageCount + " times) and is not indexed.");
                suggestion.setImpactScore(calculateImpactScore(usageCount));
                suggestions.add(suggestion);
            }
        }
        return suggestions;
    }

    private BigDecimal calculateImpactScore(long usageCount) {
        // Simple scoring logic: more usage = higher impact. Capped at 1.0.
        if (usageCount > 100) return new BigDecimal("1.00");
        if (usageCount > 50) return new BigDecimal("0.75");
        if (usageCount > 20) return new BigDecimal("0.50");
        return new BigDecimal("0.25");
    }
}
