package com.taskflow.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

/**
 * Unified error response body for all error HTTP responses.
 * Fields map is omitted when null (e.g., on 401/403/404 — only "error" is returned).
 *
 * Examples:
 *  400: { "error": "validation failed", "fields": { "email": "is required" } }
 *  401: { "error": "unauthorized" }
 *  403: { "error": "forbidden" }
 *  404: { "error": "not found" }
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String error,
        Map<String, String> fields
) {}
