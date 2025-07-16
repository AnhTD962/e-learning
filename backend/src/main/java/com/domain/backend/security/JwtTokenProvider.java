package com.domain.backend.security;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Lớp tiện ích để tạo, xác thực và phân tích cú pháp JWT.
 */
@Component
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Autowired
    private JwtConfig jwtConfig; // Sử dụng JwtConfig để lấy secret và expiration

    /**
     * Tạo một JWT token cho người dùng đã xác thực.
     *
     * @param authentication Đối tượng xác thực chứa chi tiết người dùng.
     * @return Chuỗi JWT token đã tạo.
     */
    public String generateToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getEmail())) // Chủ thể là email của người dùng
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtConfig.getJwtExpirationMs())) // Đặt thời gian hết hạn
                .signWith(jwtConfig.jwtSecretKey(), SignatureAlgorithm.HS256) // Ký bằng thuật toán HS512 và khóa bí mật
                .compact();
    }

    /**
     * Trích xuất email người dùng từ JWT token.
     *
     * @param token Chuỗi JWT token.
     * @return Email người dùng.
     */
    public String getUserEmailFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(jwtConfig.jwtSecretKey()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Xác thực JWT token.
     *
     * @param authToken Chuỗi JWT token.
     * @return True nếu token hợp lệ, ngược lại là false.
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtConfig.jwtSecretKey()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Token JWT không hợp lệ: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT đã hết hạn: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Token JWT không được hỗ trợ: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Chuỗi claims JWT trống: {}", e.getMessage());
        }

        return false;
    }
}