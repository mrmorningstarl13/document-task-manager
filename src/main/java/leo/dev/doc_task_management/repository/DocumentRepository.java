package leo.dev.doc_task_management.repository;

import leo.dev.doc_task_management.entity.Document;
import leo.dev.doc_task_management.entity.Project;
import leo.dev.doc_task_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findAllByProject(Project project);

    List<Document> findAllByOwner(User owner);

    Optional<Document> findByIdAndProject(Long id, Project project);
}
