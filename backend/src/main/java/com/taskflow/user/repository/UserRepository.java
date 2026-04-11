package com.taskflow.user.repository;

import com.taskflow.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * OCP: extend query needs by adding methods here — never modify existing ones.
 * DIP: callers depend on this interface, not a concrete datasource implementation.
 */
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
