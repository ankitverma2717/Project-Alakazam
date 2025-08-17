package org.project.alakazam.projectalakazam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueryPerformancePredictor {

    private final QueryFeatureExtractor featureExtractor;

    @Autowired
    public QueryPerformancePredictor(QueryFeatureExtractor featureExtractor) {
        this.featureExtractor = featureExtractor;
    }

    public String predictPerformance(String sqlQuery) {
        QueryFeatureExtractor.QueryFeatures features = featureExtractor.extractFeatures(sqlQuery);

        // Simple rule-based scoring algorithm
        int score = (features.joinCount() * 5)
                + (features.tableCount() * 2)
                + (features.whereClauseComplexity() * 3)
                + (features.subqueryDepth() * 10);

        if (score > 20) {
            return "Complex";
        } else if (score > 10) {
            return "Moderate";
        } else {
            return "Fast";
        }
    }
}
