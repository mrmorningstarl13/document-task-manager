package leo.dev.doc_task_management.exception;

public class AppException extends RuntimeException{
    public enum ErrorCode {
        DOCUMENT_NOT_FOUND,
        EMAIL_ALREADY_IN_USE,
        FORBIDDEN_OPERATION,
        PROJECT_NOT_FOUND,
        TASK_NOT_FOUND,
        USER_NOT_FOUND,
        INVALID_FILE_TYPE,
        INVALID_FILE_SIZE,
        MEMBER_ALREADY_EXISTS,
        EMPTY_FILE
    }

    private final ErrorCode code;

    public AppException(ErrorCode code){
        super(code.name());
        this.code = code;
    }

    public ErrorCode getCode() {
        return code;
    }
}
