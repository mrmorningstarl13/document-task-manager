package leo.dev.doc_task_management.service;

import leo.dev.doc_task_management.dto.request.LoginRequest;
import leo.dev.doc_task_management.dto.request.RegisterRequest;
import leo.dev.doc_task_management.dto.response.AuthResponse;
import leo.dev.doc_task_management.entity.Role;
import leo.dev.doc_task_management.entity.User;
import leo.dev.doc_task_management.exception.AppException;
import leo.dev.doc_task_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AuditLogService auditLogService;

    public AuthResponse register(RegisterRequest request, String ipAddress) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AppException(AppException.ErrorCode.EMAIL_ALREADY_IN_USE);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(Role.USER)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        auditLogService.log(user, "USER_REGISTER", "USER", user.getId(), "User registered", ipAddress);

        return AuthResponse.builder()
                .token(token)
                .build();
    }

    public AuthResponse login(LoginRequest request, String ipAddress) {
        userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(AppException.ErrorCode.USER_NOT_FOUND));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(AppException.ErrorCode.USER_NOT_FOUND));

        String token = jwtService.generateToken(user);

        auditLogService.log(user, "USER_LOGIN", "USER", user.getId(), "User logged in", ipAddress);

        return AuthResponse.builder()
                .token(token)
                .build();
    }
}