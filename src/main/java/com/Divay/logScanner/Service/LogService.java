package com.Divay.logScanner.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.client.ChatClient;
import com.Divay.logScanner.dto.LogAnalysisResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class LogService {
    @Value("${groq.api.key}")
    private String apiKey;

    private final ChatClient chatClient;

    public LogService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public List<LogAnalysisResponse> analyzeLogs(InputStream inputStream) throws IOException {
        Map<String, Integer> errorCounts = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.toUpperCase().contains("ERROR")) {
                    String exceptionName = parseExceptionName(line);
                    errorCounts.put(exceptionName, errorCounts.getOrDefault(exceptionName, 0) + 1);
                }
            }
        }

        if (errorCounts.isEmpty()) return Collections.emptyList();

        String allErrors = String.join(", ", errorCounts.keySet());

        // PROMPT UPDATE: Strict formatting for Root Cause and Fix
        String prompt = "Act as a Java Expert. I have these errors: [" + allErrors + "]. " +
                "For EACH error, provide its root cause and a 1-sentence fix. " +
                "STRICT FORMAT: 'ErrorName | RootCauseContent | FixContent'. " +
                "Do not write anything else.";

        String aiResponse = chatClient.prompt().user(prompt).call().content();

        return errorCounts.entrySet().stream()
                .map(entry -> {
                    String errorType = entry.getKey();
                    // AI response se dono cheezein nikalna
                    String[] details = extractDetails(aiResponse, errorType);

                    return new LogAnalysisResponse(
                            errorType,
                            entry.getValue(),
                            details[0], // explanation (Root Cause)
                            details[1]  // suggestedFix
                    );
                })
                .toList();
    }

    // New Smart Extractor
    private String[] extractDetails(String fullResponse, String errorName) {
        if (fullResponse == null) return new String[]{"No analysis available", "Check docs"};

        return Arrays.stream(fullResponse.split("\n"))
                .filter(line -> line.contains(errorName))
                .findFirst()
                .map(line -> {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 3) {
                        return new String[]{parts[1].trim(), parts[2].trim()};
                    }
                    return new String[]{"Root cause analysis failed", line};
                })
                .orElse(new String[]{"No specific root cause found", "Verify log trace"});
    }

    private String parseExceptionName(String line) {
        if (line.contains("Exception")) {
            String[] parts = line.split(" ");
            for (String part : parts) {
                if (part.contains("Exception")) {
                    return part.replaceAll("[^a-zA-Z.]", "");
                }
            }
        }
        return "General Runtime Error";
    }
}