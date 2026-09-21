package com.apisec.apilab.catalog;

import java.util.List;

public record QuizQuestion(
        String id,
        String prompt,
        List<String> choices,
        int correctIndex,
        String explanation
) {
}
