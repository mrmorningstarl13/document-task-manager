package leo.dev.doc_task_management.repository;

import leo.dev.doc_task_management.entity.Project;
import leo.dev.doc_task_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findAllByDeletedAtIsNull();

    List<Project> findAllByOwnerAndDeletedAtIsNull(User owner);

    List<Project> findAllByMembersContainingAndDeletedAtIsNull(User member);

    Optional<Project> findByIdAndDeletedAtIsNull(Long id);
}
