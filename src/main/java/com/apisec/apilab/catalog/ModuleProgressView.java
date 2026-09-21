package com.apisec.apilab.catalog;

public record ModuleProgressView(
        LearningModule module,
        boolean completed,
        String statusLabel
) {
}
