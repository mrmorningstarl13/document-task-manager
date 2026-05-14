package leo.dev.doc_task_management.repository;

import leo.dev.doc_task_management.entity.AuditLog;
import leo.dev.doc_task_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findAllByAction(String action);
    List<AuditLog> findAllByUserId(Long userId);
}
