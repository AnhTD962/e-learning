package com.domain.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.web.AuthenticationEntryPoint; // Import AuthenticationEntryPoint

import java.io.IOException;

/**
 * Bộ lọc tùy chỉnh để phân tích cú pháp JWT từ header yêu cầu và xác thực người dùng.
 * Bộ lọc này thực thi một lần mỗi yêu cầu.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationEntryPoint unauthorizedHandler; // Để xử lý lỗi xác thực

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Lấy JWT từ header Authorization
            String jwt = parseJwt(request);
            if (jwt != null && jwtTokenProvider.validateToken(jwt)) {
                // Trích xuất email từ JWT
                String email = jwtTokenProvider.getUserEmailFromToken(jwt);

                // Tải chi tiết người dùng theo email
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // Tạo đối tượng xác thực
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null, // Thông tin đăng nhập không được lưu trữ ở đây
                                userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Đặt xác thực trong SecurityContextHolder
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Không thể đặt xác thực người dùng: {}", e.getMessage());
            // Nếu có lỗi trong quá trình xác thực JWT, ủy quyền cho AuthenticationEntryPoint
            unauthorizedHandler.commence(request, response, new org.springframework.security.core.AuthenticationException("Authentication failed: " + e.getMessage()) {});
            return; // Dừng chuỗi bộ lọc
        }

        filterChain.doFilter(request, response); // Tiếp tục chuỗi bộ lọc
    }

    /**
     * Trích xuất JWT từ header Authorization (token Bearer).
     *
     * @param request HttpServletRequest
     * @return Chuỗi JWT hoặc null nếu không tìm thấy.
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // Xóa tiền tố "Bearer "
        }

        return null;
    }
}