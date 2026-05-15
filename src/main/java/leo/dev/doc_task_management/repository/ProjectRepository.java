package leo.dev.doc_task_management.repository;

import leo.dev.doc_task_management.entity.Project;
import leo.dev.doc_task_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findAllByDeletedAtIsNull();

    Optional<Project> findByIdAndDeletedAtIsNull(Long id);

    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN p.members m WHERE (p.owner = :user OR m = :user) AND p.deletedAt IS NULL")
    List<Project> findAllByOwnerOrMember(@Param("user") User user);
}
