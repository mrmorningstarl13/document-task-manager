package leo.dev.doc_task_management.service;

import leo.dev.doc_task_management.dto.request.AddMemberRequest;
import leo.dev.doc_task_management.dto.request.CreateProjectRequest;
import leo.dev.doc_task_management.dto.request.UpdateProjectRequest;
import leo.dev.doc_task_management.dto.response.ProjectResponse;
import leo.dev.doc_task_management.entity.Project;
import leo.dev.doc_task_management.entity.ProjectStatus;
import leo.dev.doc_task_management.entity.User;
import leo.dev.doc_task_management.exception.ProjectNotFoundException;
import leo.dev.doc_task_management.exception.ForbiddenOperationException;
import leo.dev.doc_task_management.exception.UserNotFoundException;
import leo.dev.doc_task_management.repository.ProjectRepository;
import leo.dev.doc_task_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectResponse createProject(User currentUser, CreateProjectRequest request) {
        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .status(ProjectStatus.ACTIVE)
                .owner(currentUser)
                .members(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        projectRepository.save(project);
        return ProjectResponse.fromEntity(project);
    }

    public ProjectResponse addMember(User currentUser, Long projectId, AddMemberRequest request) {
        Project project = projectRepository.findByIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + projectId));

        if (project.getOwner().getId() !=currentUser.getId()) {
            throw new ForbiddenOperationException("Only the project owner can add members");
        }

        User userToAdd = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + request.getUserId()));

        if (project.getMembers().contains(userToAdd)) {
            throw new RuntimeException("User is already a member of this project");
        }

        project.getMembers().add(userToAdd);
        project.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(project);
        return ProjectResponse.fromEntity(project);
    }

    public ProjectResponse updateProject(User currentUser, Long projectId, UpdateProjectRequest request) {
        Project project = projectRepository.findByIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + projectId));

        if (project.getOwner().getId() != currentUser.getId()) {
            throw new ForbiddenOperationException("Only the project owner can update the project");
        }

        if (request.getName() != null) project.setName(request.getName());
        if (request.getDescription() != null) project.setDescription(request.getDescription());
        if (request.getStatus() != null) project.setStatus(ProjectStatus.valueOf(request.getStatus().toUpperCase()));

        project.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(project);
        return ProjectResponse.fromEntity(project);
    }

    public List<ProjectResponse> getAllProjects(boolean includeDeleted) {
        List<Project> projects = includeDeleted
                ? projectRepository.findAll()
                : projectRepository.findAllByDeletedAtIsNull();

        return projects.stream()
                .map(ProjectResponse::fromEntity)
                .toList();
    }

    public List<ProjectResponse> getMyProjects(User currentUser) {
        List<Project> owned = projectRepository.findAllByOwnerAndDeletedAtIsNull(currentUser);
        List<Project> member = projectRepository.findAllByMembersContainingAndDeletedAtIsNull(currentUser);

        return Stream.concat(owned.stream(), member.stream())
                .distinct()
                .map(ProjectResponse::fromEntity)
                .toList();
    }

    public void deleteProject(User currentUser, Long projectId) {
        Project project = projectRepository.findByIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + projectId));

        if (project.getOwner().getId() != currentUser.getId()) {
            throw new ForbiddenOperationException("Only the project owner can delete the project");
        }

        project.setDeletedAt(LocalDateTime.now());
        projectRepository.save(project);
    }
}