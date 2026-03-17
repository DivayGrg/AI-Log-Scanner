package com.Divay.logScanner.dto;

public record LogAnalysisResponse(
        String errorType,
        int count,
        String explanation,
        String suggestedFix) {
}