package leo.dev.doc_task_management.service;

import leo.dev.doc_task_management.dto.request.UpdateUserRequest;
import leo.dev.doc_task_management.dto.response.UserResponse;
import leo.dev.doc_task_management.entity.Role;
import leo.dev.doc_task_management.entity.User;
import leo.dev.doc_task_management.exception.AppException;
import leo.dev.doc_task_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public UserResponse getProfile(User currentUser) {
        return UserResponse.fromEntity(currentUser);
    }

    public UserResponse updateProfile(User currentUser, UpdateUserRequest request) {
        if (request.getFirstName() != null) currentUser.setFirstName(request.getFirstName());
        if (request.getLastName() != null) currentUser.setLastName(request.getLastName());
        if (request.getEmail() != null) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new AppException(AppException.ErrorCode.EMAIL_ALREADY_IN_USE);
            }
            currentUser.setEmail(request.getEmail());
        }
        if (request.getPassword() != null) {
            currentUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        currentUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(currentUser);
        log.info("User profile updated: {}", currentUser.getEmail());
        return UserResponse.fromEntity(currentUser);
    }

    // ADMIN methods
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::fromEntity)
                .toList();
    }

    public UserResponse changeUserRole(Long userId, String role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(AppException.ErrorCode.USER_NOT_FOUND));
        user.setRole(Role.valueOf(role.toUpperCase()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        log.info("User role changed: {} to {}", user.getEmail(), role);
        return UserResponse.fromEntity(user);
    }

    public UserResponse deactivateUser(Long userId, User currentUser) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(AppException.ErrorCode.USER_NOT_FOUND));
        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        log.info("User deactivated: {}", user.getEmail());
        auditLogService.log(currentUser, "USER_DEACTIVATE", "USER",
                user.getId(), "Deactivated user: " + user.getEmail(), null);

        return UserResponse.fromEntity(user);
    }
}