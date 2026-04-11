package com.taskflow.user.service;

import com.taskflow.user.model.User;

import java.util.UUID;

/**
 * UserService — ISP: focused on user read operations needed by other services.
 * DIP: other services depend on this interface, not the JPA repository directly.
 */
public interface UserService {

    User findById(UUID id);

    User findByEmail(String email);

    java.util.List<User> listUsers();
}
