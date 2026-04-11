package com.taskflow.security;

import java.util.UUID;

/**
 * Immutable value object representing the authenticated user stored in the SecurityContext.
 *
 * Populated from JWT claims — zero database round-trips per request.
 * SRP: solely holds the authenticated identity extracted from the token.
 */
public record UserPrincipal(UUID userId, String email) {}
