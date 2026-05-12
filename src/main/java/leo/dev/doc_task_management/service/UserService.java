package leo.dev.doc_task_management.service;

import leo.dev.doc_task_management.dto.request.UpdateUserRequest;
import leo.dev.doc_task_management.dto.response.UserResponse;
import leo.dev.doc_task_management.entity.Role;
import leo.dev.doc_task_management.entity.User;
import leo.dev.doc_task_management.exception.EmailAlreadyInUseException;
import leo.dev.doc_task_management.exception.UserNotFoundException;
import leo.dev.doc_task_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse getProfile(User currentUser) {
        return UserResponse.fromEntity(currentUser);
    }

    public UserResponse updateProfile(User currentUser, UpdateUserRequest request) {
        if (request.getFirstName() != null) currentUser.setFirstName(request.getFirstName());
        if (request.getLastName() != null) currentUser.setLastName(request.getLastName());
        if (request.getEmail() != null) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new EmailAlreadyInUseException("Email already in use: " + request.getEmail());
            }
            currentUser.setEmail(request.getEmail());
        }
        if (request.getPassword() != null) {
            currentUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        currentUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(currentUser);
        return UserResponse.fromEntity(currentUser);
    }

    // ADMIN methods
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::fromEntity)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse changeUserRole(Long userId, String role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        user.setRole(Role.valueOf(role.toUpperCase()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return UserResponse.fromEntity(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return UserResponse.fromEntity(user);
    }
}