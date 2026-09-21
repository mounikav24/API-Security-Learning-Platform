package com.apisec.apilab.controller.api;

import com.apisec.apilab.catalog.ApiEndpoint;
import com.apisec.apilab.catalog.LabPreview;
import com.apisec.apilab.catalog.LearningModule;
import com.apisec.apilab.service.LearningService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CatalogApiController {

    private final LearningService learningService;

    public CatalogApiController(LearningService learningService) {
        this.learningService = learningService;
    }

    @GetMapping("/api/catalog/modules")
    public List<LearningModule> modules() {
        return learningService.catalog().modules();
    }

    @GetMapping("/api/catalog/labs")
    public List<LabPreview> labs() {
        return learningService.catalog().labs();
    }

    @GetMapping("/api/catalog/explorer")
    public List<ApiEndpoint> explorer() {
        return learningService.catalog().explorer();
    }
}
