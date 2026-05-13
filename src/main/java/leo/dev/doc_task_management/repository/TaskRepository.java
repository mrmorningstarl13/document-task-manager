package leo.dev.doc_task_management.repository;

import leo.dev.doc_task_management.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByProject(Project project);

    List<Task> findAllByProjectAndStatus(Project project, TaskStatus status);

    List<Task> findAllByProjectAndPriority(Project project, TaskPriority priority);

    List<Task> findAllByProjectAndStatusAndPriority(Project project, TaskStatus status, TaskPriority priority);

    List<Task> findAllByAssignedTo(User user);
}
