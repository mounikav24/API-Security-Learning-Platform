package com.apisec.apilab.catalog;

import java.util.List;

public record DashboardSnapshot(
        int moduleCount,
        int completedModules,
        int labCount,
        int questionCount,
        Integer lastAssessmentPercent,
        LearningModule nextModule,
        List<ModuleProgressView> modules
) {
}
