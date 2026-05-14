package leo.dev.doc_task_management.controller;

import leo.dev.doc_task_management.dto.response.AuditLogResponse;
import leo.dev.doc_task_management.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<List<AuditLogResponse>> getAllLogs(
            @RequestParam(required = false) String action) {
        if (action != null) {
            return ResponseEntity.ok(auditLogService.getLogsByAction(action));
        }
        return ResponseEntity.ok(auditLogService.getAllLogs());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<AuditLogResponse>> getLogsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(auditLogService.getLogsByUser(userId));
    }
}