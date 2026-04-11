package com.taskflow.task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskflow.BaseIntegrationTest;
import com.taskflow.security.JwtService;
import com.taskflow.task.dto.CreateTaskRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TaskControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    private String validToken;

    @BeforeEach
    void setUp() {
        // Authenticate as our admin user from seed.sql
        com.taskflow.user.model.User admin = new com.taskflow.user.model.User();
        admin.setId(UUID.fromString("00000000-0000-0000-0000-000000000010"));
        admin.setEmail("admin@example.com");
        validToken = jwtService.generateToken(admin);
    }

    @Test
    @Sql(scripts = "/seed.sql")
    void getTasksByProject_ReturnsTasks() throws Exception {
        mockMvc.perform(get("/projects/11111111-1111-1111-1111-111111111111/tasks")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].title").value("Test Task 1"));
    }

    @Test
    @Sql(scripts = "/seed.sql")
    void createTask_ValidRequest_Success() throws Exception {
        CreateTaskRequest req = new CreateTaskRequest("New IT Task", "Desc", "TODO", "HIGH", 
                                                      UUID.fromString("00000000-0000-0000-0000-000000000020"), 
                                                      LocalDate.now().plusDays(1), 3);

        mockMvc.perform(post("/projects/11111111-1111-1111-1111-111111111111/tasks")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.title").value("New IT Task"));
    }
}
