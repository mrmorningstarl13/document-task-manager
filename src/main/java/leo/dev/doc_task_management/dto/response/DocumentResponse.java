package leo.dev.doc_task_management.dto.response;

import leo.dev.doc_task_management.entity.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
    private Long id;
    private String name;
    private String type;
    private Long size;
    private String storagePath;
    private UserResponse owner;
    private Long projectId;
    private LocalDateTime uploadedAt;

    public static DocumentResponse fromEntity(Document document) {
        return DocumentResponse.builder()
                .id(document.getId())
                .name(document.getName())
                .type(document.getType())
                .size(document.getSize())
                .storagePath(document.getStoragePath())
                .owner(UserResponse.fromEntity(document.getOwner()))
                .projectId(document.getProject().getId())
                .uploadedAt(document.getUploadedAt())
                .build();
    }
}
