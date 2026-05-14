package leo.dev.doc_task_management.service;

import leo.dev.doc_task_management.dto.response.AuditLogResponse;
import leo.dev.doc_task_management.entity.AuditLog;
import leo.dev.doc_task_management.entity.User;
import leo.dev.doc_task_management.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void log(User user, String action, String entityType, Long entityId, String details, String ipAddress) {
        AuditLog auditLog = AuditLog.builder()
                .user(user)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .details(details)
                .ipAddress(ipAddress)
                .createdAt(LocalDateTime.now())
                .build();
        auditLogRepository.save(auditLog);
    }

    public List<AuditLogResponse> getAllLogs() {
        return auditLogRepository.findAll()
                .stream()
                .map(AuditLogResponse::fromEntity)
                .toList();
    }

    public List<AuditLogResponse> getLogsByAction(String action) {
        return auditLogRepository.findAllByAction(action)
                .stream()
                .map(AuditLogResponse::fromEntity)
                .toList();
    }

    public List<AuditLogResponse> getLogsByUser(Long userId) {
        return auditLogRepository.findAllByUserId(userId)
                .stream()
                .map(AuditLogResponse::fromEntity)
                .toList();
    }
}
