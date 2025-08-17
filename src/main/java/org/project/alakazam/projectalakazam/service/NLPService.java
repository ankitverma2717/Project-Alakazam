package org.project.alakazam.projectalakazam.service;

public interface NLPService {
    String convertToSQL(String naturalLanguageQuery, String schema);
    String explainSQL(String sqlQuery);
}
