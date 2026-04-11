package com.taskflow.security;

import com.taskflow.user.model.User;

import java.util.UUID;

/**
 * JwtService — ISP: focused solely on JWT operations.
 * OCP: swap implementations (e.g., RS256, token rotation) without changing callers.
 * DIP: callers (JwtAuthFilter, AuthServiceImpl) depend on this interface.
 */
public interface JwtService {

    /** Generate a signed JWT containing userId and email claims. */
    String generateToken(User user);

    /** Extract the userId (subject) from a valid token. */
    UUID extractUserId(String token);

    /** Extract the email claim from a valid token. */
    String extractEmail(String token);

    /** Returns true if the token is structurally valid, signed correctly, and not expired. */
    boolean isTokenValid(String token);
}
