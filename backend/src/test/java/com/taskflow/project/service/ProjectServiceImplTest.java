package com.taskflow.project.service;

import com.taskflow.exception.ForbiddenException;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.project.dto.ProjectResponse;
import com.taskflow.project.mapper.ProjectMapper;
import com.taskflow.project.model.Project;
import com.taskflow.project.repository.ProjectRepository;
import com.taskflow.task.repository.TaskRepository;
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
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private User owner;
    private Project project;
    private UUID ownerId = UUID.randomUUID();
    private UUID projectId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(ownerId);

        project = new Project();
        project.setId(projectId);
        project.setOwner(owner);
    }

    @Test
    void getProjectById_ValidOwner_ReturnsProject() {
        when(projectRepository.findByIdWithTasks(projectId)).thenReturn(Optional.of(project));
        when(projectMapper.toDetailResponse(project)).thenReturn(null); // Just care about execution flow

        projectService.getProjectById(projectId, ownerId);

        verify(projectRepository).findByIdWithTasks(projectId);
        verify(projectMapper).toDetailResponse(project);
    }

    @Test
    void getProjectById_NonOwnerNoTasks_ThrowsForbidden() {
        when(projectRepository.findByIdWithTasks(projectId)).thenReturn(Optional.of(project));
        project.setTasks(Collections.emptyList());

        assertThrows(ForbiddenException.class, () -> projectService.getProjectById(projectId, UUID.randomUUID()));
    }

    @Test
    void deleteProject_NonOwner_ThrowsForbidden() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        
        assertThrows(ForbiddenException.class, () -> projectService.deleteProject(projectId, UUID.randomUUID()));
        verify(projectRepository, never()).delete(any());
    }
}
