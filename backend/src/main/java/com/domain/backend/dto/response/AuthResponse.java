package com.domain.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data // Lombok annotation
@NoArgsConstructor // Lombok annotation cho constructor không đối số
@AllArgsConstructor // Lombok annotation cho constructor tất cả đối số
public class AuthResponse { // Đã đổi tên từ JwtResponse
    private String token;
    private String type = "Bearer"; // Loại token
    private String id;
    private String username;
    private String email;
    private List<String> roles;

    public AuthResponse(String accessToken, String id, String username, String email, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}
