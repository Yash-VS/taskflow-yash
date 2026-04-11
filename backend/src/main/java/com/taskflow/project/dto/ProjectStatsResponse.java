package com.taskflow.project.dto;

import java.util.Map;

public record ProjectStatsResponse(
        Map<String, Long> tasksByStatus,
        Map<String, Long> tasksByAssignee
) {}
