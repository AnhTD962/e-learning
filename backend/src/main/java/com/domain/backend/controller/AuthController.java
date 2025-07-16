package com.domain.backend.controller;

import com.domain.backend.dto.request.LoginRequest;
import com.domain.backend.dto.request.RegisterRequest;
import com.domain.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600) // Cho phép các yêu cầu cross-origin từ bất kỳ domain nào
@RestController // Đánh dấu lớp này là một REST controller
@RequestMapping("/api/auth") // Đường dẫn cơ sở cho các endpoint liên quan đến xác thực
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Endpoint để đăng ký người dùng.
     *
     * @param registerRequest Payload yêu cầu đăng ký.
     * @return ResponseEntity cho biết đăng ký thành công hay thất bại.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        return authService.registerUser(registerRequest);
    }

    /**
     * Endpoint để đăng nhập người dùng.
     *
     * @param loginRequest Payload yêu cầu đăng nhập.
     * @return ResponseEntity với token JWT và chi tiết người dùng khi đăng nhập thành công.
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.authenticateUser(loginRequest);
    }
}
