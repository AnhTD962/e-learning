package com.domain.backend.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Xử lý các nỗ lực truy cập trái phép.
 * Lớp này được gọi khi một người dùng chưa được xác thực cố gắng truy cập một tài nguyên HTTP bảo mật.
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        logger.error("Lỗi trái phép: {}", authException.getMessage());
        // Gửi phản hồi 401 Unauthorized
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Lỗi: Không được ủy quyền");
    }
}