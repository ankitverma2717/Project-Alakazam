package org.project.alakazam.projectalakazam.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class GeminiNLPService implements NLPService {

    private final WebClient webClient;

    @Value("${app.gemini.api.key}")
    private String apiKey;

    @Value("${app.gemini.api.url}")
    private String apiUrl;

    // Use constructor injection for the WebClient.Builder
    public GeminiNLPService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    // These records are modern Java classes for representing the API's JSON structure.
    private record GeminiRequest(List<Content> contents) {}
    private record Content(List<Part> parts) {}
    private record Part(String text) {}

    private record GeminiResponse(List<Candidate> candidates) {}
    private record Candidate(Content content) {}


    @Override
    public String convertToSQL(String naturalLanguageQuery, String schema) {
        // 1. --- Construct the Prompt ---
        // This is the most important part. We give the AI clear instructions and context.
        String prompt = """
                You are a PostgreSQL expert who writes SQL queries.
                Given the following database schema and a user's question, strictly generate a single, valid PostgreSQL query.

                ### Database Schema:
                %s

                ### User Question:
                %s

                ### Instructions:
                1.  Your output MUST be ONLY the SQL query.
                2.  You MUST NOT use any tables or columns that are not explicitly listed in the schema provided.
                3.  If the user's question cannot be answered using ONLY the provided schema, you MUST return a single line of text starting with "ERROR:", followed by a brief explanation of why. For example: "ERROR: The database does not contain a 'products' table."
                4.  Do NOT include any markdown formatting like ```sql in your final answer.
                """.formatted(schema, naturalLanguageQuery);


        // 2. --- Build the Request Body ---
        var requestBody = new GeminiRequest(
                List.of(new Content(List.of(new Part(prompt))))
        );

        try {
            // 3. --- Make the API Call ---
            GeminiResponse response = webClient.post()
                    .uri(apiUrl)
                    .header("x-goog-api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve() // Executes the request
                    .bodyToMono(GeminiResponse.class) // Deserializes the response JSON to our record
                    .block(); // Makes the call synchronous (waits for the result)

            // 4. --- Parse and Clean the Response ---
            if (response != null && response.candidates() != null && !response.candidates().isEmpty()) {
                String generatedText = response.candidates().get(0).content().parts().get(0).text();
                // Clean up potential markdown formatting from the AI's response
                return generatedText.replace("```sql", "").replace("```", "").trim();
            }

        } catch (Exception e) {
            // In a real app, you would log this error properly
            System.err.println("Error calling Gemini API: " + e.getMessage());
            return "ERROR: Could not generate SQL from the AI service.";
        }

        return "ERROR: No valid SQL was returned from the AI service.";
    }

    @Override
    public String explainSQL(String sqlQuery) {
        // This prompt is specifically designed to get a business-friendly summary.
        String prompt = """
                You are a helpful database assistant who explains SQL queries in simple terms.
                Given the following SQL query, explain what it does in easy-to-understand business terms.
                Assume the user is non-technical. Do not explain the SQL syntax (like what SELECT or JOIN means).
                Provide a concise, one or two-sentence summary.

                ### SQL Query:
                %s
                """.formatted(sqlQuery);

        // 2. --- Build the Request Body ---
        var requestBody = new GeminiRequest(
                List.of(new Content(List.of(new Part(prompt))))
        );

        try {
            // 3. --- Make the API Call (Identical logic to convertToSQL) ---
            GeminiResponse response = webClient.post()
                    .uri(apiUrl)
                    .header("x-goog-api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(GeminiResponse.class)
                    .block();

            // 4. --- Parse and Return the Response ---
            if (response != null && response.candidates() != null && !response.candidates().isEmpty()) {
                return response.candidates().get(0).content().parts().get(0).text().trim();
            }

        } catch (Exception e) {
            System.err.println("Error calling Gemini API for explanation: " + e.getMessage());
            return "ERROR: Could not get an explanation from the AI service.";
        }

        return "ERROR: No valid explanation was returned from the AI service.";
    }
}
