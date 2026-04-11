package com.taskflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TaskFlow Application Entry Point
 *
 * Architecture follows SOLID principles:
 *  - S: Each class has one responsibility (controllers → HTTP, services → business logic, repos → data)
 *  - O: All services are exposed via interfaces, closed for modification, open for extension
 *  - L: Service implementations are interchangeable through their interfaces
 *  - I: Focused, domain-specific interfaces (AuthService, ProjectService, TaskService)
 *  - D: High-level modules depend on abstractions, not concrete implementations (Spring DI)
 */
@SpringBootApplication
public class TaskFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskFlowApplication.class, args);
    }
}
