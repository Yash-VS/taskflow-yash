package com.taskflow.auth.service;

import com.taskflow.auth.dto.AuthResponse;
import com.taskflow.auth.dto.LoginRequest;
import com.taskflow.auth.dto.RegisterRequest;

/**
 * AuthService — ISP: focused on authentication flows only (register + login).
 * DIP: AuthController depends on this interface, not AuthServiceImpl.
 */
public interface AuthService {

    /** Register a new user. Returns a JWT + user info on success. */
    AuthResponse register(RegisterRequest request);

    /** Authenticate with email/password. Returns a JWT + user info on success. */
    AuthResponse login(LoginRequest request);
}
