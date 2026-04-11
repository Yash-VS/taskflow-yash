package com.taskflow.auth.service;

import com.taskflow.auth.dto.AuthResponse;
import com.taskflow.auth.dto.LoginRequest;
import com.taskflow.auth.dto.RegisterRequest;
import com.taskflow.exception.EmailAlreadyExistsException;
import com.taskflow.security.JwtService;
import com.taskflow.user.model.User;
import com.taskflow.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AuthServiceImpl — SRP: handles user registration and login business logic only.
 *
 * Dependencies injected as interfaces (DIP):
 *  - JwtService (not JwtServiceImpl)
 *  - PasswordEncoder (not BCryptPasswordEncoder)
 *  - UserRepository (Spring Data interface)
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email is already in use");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        User saved = userRepository.save(user);
        String token = jwtService.generateToken(saved);

        return toResponse(saved, token);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // Throws BadCredentialsException on failure — caught by GlobalExceptionHandler → 401
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(); // safe — authentication already verified email exists

        String token = jwtService.generateToken(user);
        return toResponse(user, token);
    }

    private AuthResponse toResponse(User user, String token) {
        return new AuthResponse(
                token,
                new AuthResponse.UserInfo(user.getId(), user.getName(), user.getEmail())
        );
    }
}
