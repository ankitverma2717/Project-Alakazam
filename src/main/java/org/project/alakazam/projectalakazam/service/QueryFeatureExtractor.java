package org.project.alakazam.projectalakazam.service;

import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

@Service
public class QueryFeatureExtractor {

    // Simple record to hold the extracted features
    public record QueryFeatures(int tableCount, int joinCount, int whereClauseComplexity, int aggregationCount, int subqueryDepth) {}

    public QueryFeatures extractFeatures(String sqlQuery) {
        String upperQuery = sqlQuery.toUpperCase();
        return new QueryFeatures(
                countOccurrences(upperQuery, "\\sFROM\\s+|\\sJOIN\\s+"),
                countOccurrences(upperQuery, "\\sJOIN\\s+"),
                calculateWhereComplexity(upperQuery),
                countOccurrences(upperQuery, "COUNT\\(|SUM\\(|AVG\\(|MAX\\(|MIN\\("),
                calculateSubqueryDepth(upperQuery)
        );
    }

    private int countOccurrences(String text, String regex) {
        return (int) Pattern.compile(regex).matcher(text).results().count();
    }

    private int calculateWhereComplexity(String text) {
        int whereIndex = text.indexOf("WHERE");
        if (whereIndex == -1) {
            return 0;
        }
        String whereClause = text.substring(whereIndex);
        // Complexity is a simple count of logical operators
        return countOccurrences(whereClause, "AND|OR|LIKE|IN|BETWEEN");
    }

    private int calculateSubqueryDepth(String text) {
        int depth = 0;
        int maxDepth = 0;
        for (char c : text.toCharArray()) {
            if (c == '(') {
                depth++;
                if (depth > maxDepth) {
                    maxDepth = depth;
                }
            } else if (c == ')') {
                depth--;
            }
        }
        // A simple heuristic: subqueries usually start with (SELECT
        int subqueryCount = countOccurrences(text, "\\(SELECT");
        return subqueryCount > 0 ? Math.max(0, maxDepth - 1) / subqueryCount : 0;
    }
}
