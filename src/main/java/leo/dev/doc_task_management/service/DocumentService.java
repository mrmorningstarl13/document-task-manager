package leo.dev.doc_task_management.service;

import leo.dev.doc_task_management.dto.response.DocumentResponse;
import leo.dev.doc_task_management.entity.Document;
import leo.dev.doc_task_management.entity.Project;
import leo.dev.doc_task_management.entity.Role;
import leo.dev.doc_task_management.entity.User;
import leo.dev.doc_task_management.exception.AppException;
import leo.dev.doc_task_management.repository.DocumentRepository;
import leo.dev.doc_task_management.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final ProjectRepository projectRepository;
    private final MinioService minioService;
    private final AuditLogService auditLogService;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final List<String> ALLOWED_TYPES = List.of(
            "application/pdf",
            "image/png",
            "image/jpeg",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "text/plain"
    );

    public DocumentResponse uploadDocument(User currentUser, Long projectId, MultipartFile file) {
        Project project = projectRepository.findByIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new AppException(AppException.ErrorCode.PROJECT_NOT_FOUND));

        if (project.getOwner().getId() != currentUser.getId() &&
                !project.getMembers().contains(currentUser)) {
            throw new AppException(AppException.ErrorCode.FORBIDDEN_OPERATION);
        }

        if (file.isEmpty()) {
            throw new AppException(AppException.ErrorCode.EMPTY_FILE);
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new AppException(AppException.ErrorCode.INVALID_FILE_SIZE);
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new AppException(AppException.ErrorCode.INVALID_FILE_TYPE);
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String storagePath = UUID.randomUUID() + "_" + originalFilename;

            minioService.uploadFile(
                    storagePath,
                    file.getInputStream(),
                    file.getSize(),
                    file.getContentType()
            );

            Document document = Document.builder()
                    .name(originalFilename)
                    .type(file.getContentType())
                    .size(file.getSize())
                    .storagePath(storagePath)
                    .owner(currentUser)
                    .project(project)
                    .uploadedAt(LocalDateTime.now())
                    .build();

            documentRepository.save(document);

            auditLogService.log(currentUser, "DOCUMENT_UPLOAD", "DOCUMENT",
                    document.getId(), "Uploaded: " + document.getName(), null);

            log.info("Document uploaded: {} to project: {}", document.getName(), projectId);
            return DocumentResponse.fromEntity(document);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload document: " + e.getMessage());
        }
    }

    public InputStream downloadDocument(User currentUser, Long projectId, Long documentId) {
        Project project = projectRepository.findByIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new AppException(AppException.ErrorCode.PROJECT_NOT_FOUND));

        if (project.getOwner().getId() != currentUser.getId() &&
                !project.getMembers().contains(currentUser)) {
            throw new AppException(AppException.ErrorCode.FORBIDDEN_OPERATION);
        }

        Document document = documentRepository.findByIdAndProject(documentId, project)
                .orElseThrow(() -> new AppException(AppException.ErrorCode.DOCUMENT_NOT_FOUND));

        log.info("Document downloaded: {} by: {}", document.getName(), currentUser.getEmail());
        return minioService.downloadFile(document.getStoragePath());
    }

    public List<DocumentResponse> listDocuments(User currentUser, Long projectId) {
        Project project = projectRepository.findByIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new AppException(AppException.ErrorCode.PROJECT_NOT_FOUND));

        if (project.getOwner().getId() != currentUser.getId() &&
                !project.getMembers().contains(currentUser)) {
            throw new AppException(AppException.ErrorCode.FORBIDDEN_OPERATION);
        }

        return documentRepository.findAllByProject(project)
                .stream()
                .map(DocumentResponse::fromEntity)
                .toList();
    }

    public void deleteDocument(User currentUser, Long projectId, Long documentId) {
        Project project = projectRepository.findByIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new AppException(AppException.ErrorCode.PROJECT_NOT_FOUND));

        Document document = documentRepository.findByIdAndProject(documentId, project)
                .orElseThrow(() -> new AppException(AppException.ErrorCode.DOCUMENT_NOT_FOUND));

        if (document.getOwner().getId() != currentUser.getId() &&
                project.getOwner().getId() != currentUser.getId() &&
                !currentUser.getRole().equals(Role.ADMIN)) {
            throw new AppException(AppException.ErrorCode.FORBIDDEN_OPERATION);
        }

        minioService.deleteFile(document.getStoragePath());
        documentRepository.delete(document);

        log.info("Document deleted: {} by: {}", document.getName(), currentUser.getEmail());
        auditLogService.log(currentUser, "DOCUMENT_DELETE", "DOCUMENT",
                document.getId(), "Deleted: " + document.getName(), null);
    }
}
