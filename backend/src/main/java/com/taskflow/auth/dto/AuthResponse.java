package com.taskflow.auth.dto;

import java.util.UUID;

/** Response body for both /auth/register and /auth/login */
public record AuthResponse(
        String token,
        UserInfo user
) {
    /** Nested user info — avoids exposing the full User entity */
    public record UserInfo(
            UUID id,
            String name,
            String email
    ) {}
}
