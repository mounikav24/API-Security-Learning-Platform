package com.apisec.apilab.catalog;

import java.util.List;

public record LabPreview(
        String slug,
        String title,
        String difficulty,
        String relatedModuleSlug,
        String summary,
        List<String> objectives,
        String status
) {
}
