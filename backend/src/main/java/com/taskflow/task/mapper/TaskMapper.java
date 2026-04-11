package com.taskflow.task.mapper;

import com.taskflow.task.dto.TaskResponse;
import com.taskflow.task.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * TaskMapper — SRP: entity → DTO conversion only.
 * componentModel = "spring" means MapStruct generates a @Component — injected by Spring.
 *
 * Status and priority are mapped as lowercase strings using expression mapping,
 * so the API always returns "todo"/"in_progress"/"done" regardless of Java enum name.
 */
@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "projectId",    source = "project.id")
    @Mapping(target = "assigneeId",   source = "assignee.id")   // nullable — MapStruct handles null
    @Mapping(target = "assigneeName", source = "assignee.name")
    @Mapping(target = "creatorId",    source = "creator.id")
    @Mapping(target = "status",   expression = "java(task.getStatus().name().toLowerCase())")
    @Mapping(target = "priority", expression = "java(task.getPriority().name().toLowerCase())")
    TaskResponse toResponse(Task task);
}
