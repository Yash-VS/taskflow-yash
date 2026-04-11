package com.taskflow.user.service;

import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.user.model.User;
import com.taskflow.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);
        user.setEmail("user@example.com");
    }

    @Test
    void findById_ExistingUser_ReturnsUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.findById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
    }

    @Test
    void findById_NonExistingUser_ThrowsException() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findById(userId));
    }

    @Test
    void listUsers_ReturnsAll() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> result = userService.listUsers();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }
}
