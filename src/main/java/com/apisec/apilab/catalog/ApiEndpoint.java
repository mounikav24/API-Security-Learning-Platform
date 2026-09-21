package com.apisec.apilab.catalog;

public record ApiEndpoint(
        String method,
        String path,
        String description,
        String auth,
        String sampleResponse
) {
}
