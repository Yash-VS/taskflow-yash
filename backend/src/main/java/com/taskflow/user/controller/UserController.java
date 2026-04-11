package com.taskflow.user.controller;

import com.taskflow.user.dto.UserInfoResponse;
import com.taskflow.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserInfoResponse>> listUsers() {
        return ResponseEntity.ok(
                userService.listUsers().stream()
                        .map(u -> new UserInfoResponse(u.getId(), u.getName(), u.getEmail()))
                        .toList()
        );
    }
}
