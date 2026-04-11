package com.taskflow.project.mapper;

import com.taskflow.project.dto.ProjectDetailResponse;
import com.taskflow.project.dto.ProjectResponse;
import com.taskflow.project.model.Project;
import com.taskflow.task.mapper.TaskMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * ProjectMapper — SRP: entity → DTO conversion only.
 *
 * Uses TaskMapper (via `uses`) for converting the nested tasks list.
 * ownerId is sourced from the owner relationship — avoids exposing the full User object.
 */
@Mapper(componentModel = "spring", uses = {TaskMapper.class})
public interface ProjectMapper {

    /** Maps Project → ProjectResponse (list view, no tasks, taskCount from @Formula) */
    @Mapping(target = "ownerId",   source = "owner.id")
    @Mapping(target = "ownerName", source = "owner.name")
    ProjectResponse toResponse(Project project);

    /** Maps Project → ProjectDetailResponse (full view with tasks list) */
    @Mapping(target = "ownerId",   source = "owner.id")
    @Mapping(target = "ownerName", source = "owner.name")
    @Mapping(target = "tasks",     source = "tasks")   // delegates each Task to TaskMapper
    ProjectDetailResponse toDetailResponse(Project project);
}
