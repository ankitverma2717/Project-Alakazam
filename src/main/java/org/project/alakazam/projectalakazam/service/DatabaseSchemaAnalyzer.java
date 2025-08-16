package org.project.alakazam.projectalakazam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Optional;

@Service
public class DatabaseSchemaAnalyzer {

    @Autowired
    private DataSource dataSource;

    public Optional<String> getSchemaAsCreateTableStatements() {
        StringBuilder schemaBuilder = new StringBuilder();
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            // --- THE CRUCIAL FIX IS HERE ---
            // We changed the schemaPattern from "public" to null.
            // This tells the JDBC driver to search in all schemas accessible to the current user,
            // which is much more reliable than assuming the "public" schema.
            try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    // Filter out our internal application tables
                    if ("query_history".equals(tableName) || "performance_metrics".equals(tableName) || "index_suggestions".equals(tableName)) {
                        continue;
                    }

                    schemaBuilder.append("CREATE TABLE ").append(tableName).append(" (\n");

                    // We also pass null for the schema here to be consistent.
                    try (ResultSet columns = metaData.getColumns(null, null, tableName, "%")) {
                        while (columns.next()) {
                            String columnName = columns.getString("COLUMN_NAME");
                            String columnType = columns.getString("TYPE_NAME");
                            String finalType = mapJdbcTypeToSql(columnType);
                            schemaBuilder.append("  ").append(columnName).append(" ").append(finalType).append(",\n");
                        }
                    }
                    if (schemaBuilder.toString().contains(",")) {
                        schemaBuilder.setLength(schemaBuilder.length() - 2);
                    }
                    schemaBuilder.append("\n);\n\n");
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to analyze database schema: " + e.getMessage());
            return Optional.empty();
        }

        String resultSchema = schemaBuilder.toString();
        if (resultSchema.isBlank()) {
            return Optional.empty();
        } else {
            return Optional.of(resultSchema);
        }
    }

    private String mapJdbcTypeToSql(String jdbcType) {
        return switch (jdbcType.toLowerCase()) {
            case "varchar", "text" -> "TEXT";
            case "int4", "serial" -> "INTEGER";
            case "date" -> "DATE";
            case "timestamp" -> "TIMESTAMP";
            default -> jdbcType.toUpperCase();
        };
    }
}