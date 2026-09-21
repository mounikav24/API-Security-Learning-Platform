package com.apisec.apilab.catalog;

import java.util.List;

public record LearningModule(
        String slug,
        String code,
        String title,
        String level,
        int durationMinutes,
        String summary,
        List<String> objectives,
        List<Lesson> lessons,
        List<String> keyTakeaways
) {
}
