package com.domain.backend.service;

import com.domain.backend.dto.request.LoginRequest;
import com.domain.backend.dto.request.RegisterRequest;
import com.domain.backend.dto.response.AuthResponse;
import com.domain.backend.dto.response.MessageResponse;
import com.domain.backend.entity.User;
import com.domain.backend.exception.ValidationException;
import com.domain.backend.repository.UserRepository;
import com.domain.backend.security.JwtTokenProvider;
import com.domain.backend.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Dịch vụ xử lý logic nghiệp vụ liên quan đến xác thực người dùng.
 */
@Service// Đánh dấu lớp này là một Spring Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * Đăng ký một người dùng mới.
     *
     * @param registerRequest Yêu cầu đăng ký chứa tên người dùng, email và mật khẩu.
     * @return ResponseEntity với thông báo thành công hoặc lỗi nếu email đã được sử dụng.
     */
    public ResponseEntity<?> registerUser(RegisterRequest registerRequest) {
        // Kiểm tra xem email đã được sử dụng chưa
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ValidationException("Lỗi: Email đã được sử dụng!");
        }

        // Tạo tài khoản người dùng mới
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        // Mã hóa mật khẩu trước khi lưu
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        // Gán vai trò mặc định (ví dụ: "STUDENT")
        user.getRoles().add("STUDENT");

        userRepository.save(user); // Lưu người dùng vào MongoDB

        return ResponseEntity.ok(new MessageResponse("Người dùng đã đăng ký thành công!"));
    }

    /**
     * Xác thực người dùng và tạo token JWT.
     *
     * @param loginRequest Yêu cầu đăng nhập chứa email và mật khẩu.
     * @return ResponseEntity với JWT và chi tiết người dùng khi đăng nhập thành công.
     */
    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        // Xác thực thông tin đăng nhập người dùng
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        // Đặt xác thực trong SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Tạo token JWT
        String jwt = jwtTokenProvider.generateToken(authentication);

        // Lấy chi tiết người dùng từ đối tượng xác thực
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Trích xuất vai trò
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Trả về phản hồi JWT
        return ResponseEntity.ok(new AuthResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }
}
