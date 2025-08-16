package org.project.alakazam.projectalakazam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class QueryExecutionService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public QueryExecutionService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long executeAndMeasure(String sqlQuery) {
        // We only execute read-only queries for safety for now.
        if (!sqlQuery.trim().toUpperCase().startsWith("SELECT")) {
            // In a real app, you might handle this differently,
            // but for now, we refuse to run non-SELECT queries.
            return -1; // Indicate that the query was not run
        }

        long startTime = System.currentTimeMillis();
        try {
            // We use queryForList as a generic way to execute a select statement.
            // We don't care about the results here, only the execution time.
            jdbcTemplate.queryForList(sqlQuery);
        } catch (Exception e) {
            System.err.println("Error executing user query: " + e.getMessage());
            return -2; // Indicate an error during execution
        }
        long endTime = System.currentTimeMillis();

        return endTime - startTime;
    }
}
