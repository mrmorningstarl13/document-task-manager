package leo.dev.doc_task_management.controller;


import leo.dev.doc_task_management.dto.response.DocumentResponse;
import leo.dev.doc_task_management.entity.User;
import leo.dev.doc_task_management.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponse> uploadDocument(@AuthenticationPrincipal User currentUser,
                                                           @PathVariable Long projectId,
                                                           @RequestPart("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(documentService.uploadDocument(currentUser, projectId, file));
    }

    @GetMapping("/{documentId}/download")
    public ResponseEntity<Resource> downloadDocument(@AuthenticationPrincipal User currentUser,
                                                     @PathVariable Long projectId,
                                                     @PathVariable Long documentId) {
        InputStream inputStream = documentService.downloadDocument(currentUser, projectId, documentId);
        InputStreamResource resource = new InputStreamResource(inputStream);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment")
                .body((Resource) resource);
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> listDocuments(@AuthenticationPrincipal User currentUser,
                                                                @PathVariable Long projectId) {
        return ResponseEntity.ok(documentService.listDocuments(currentUser, projectId));
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> deleteDocument(@AuthenticationPrincipal User currentUser,
                                               @PathVariable Long projectId,
                                               @PathVariable Long documentId) {
        documentService.deleteDocument(currentUser, projectId, documentId);
        return ResponseEntity.noContent().build();
    }
}
