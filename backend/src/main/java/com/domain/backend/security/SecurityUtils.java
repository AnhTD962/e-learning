package com.domain.backend.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Optional;

/**
 * Lớp tiện ích để truy cập thông tin bảo mật.
 */
public class SecurityUtils {

    /**
     * Lấy ID của người dùng đã xác thực hiện tại.
     *
     * @return ID của người dùng đã xác thực.
     * @throws ResponseStatusException nếu không có người dùng nào được xác thực.
     */
    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Người dùng chưa được xác thực.");
        }
        if (authentication.getPrincipal() instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) authentication.getPrincipal()).getId();
        }
        // Xử lý các trường hợp khác nếu principal không phải là UserDetailsImpl (ví dụ: là String "anonymousUser")
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Không thể lấy ID người dùng đã xác thực.");
    }

    /**
     * Lấy email của người dùng đã xác thực hiện tại.
     *
     * @return Email của người dùng đã xác thực.
     */
    public static Optional<String> getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.empty();
        }
        if (authentication.getPrincipal() instanceof UserDetailsImpl) {
            return Optional.of(((UserDetailsImpl) authentication.getPrincipal()).getEmail());
        }
        return Optional.empty();
    }

    /**
     * Kiểm tra xem người dùng đã xác thực hiện tại có vai trò cụ thể hay không.
     *
     * @param role Vai trò cần kiểm tra (ví dụ: "ADMIN", "TEACHER", "STUDENT").
     * @return True nếu người dùng có vai trò, ngược lại là false.
     */
    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream().anyMatch(a -> a.getAuthority().equals(role));
    }

    /**
     * Kiểm tra xem người dùng hiện tại có phải là ADMIN hay không.
     *
     * @return True nếu là ADMIN, ngược lại là false.
     */
    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * Kiểm tra xem người dùng hiện tại có phải là TEACHER hay không.
     *
     * @return True nếu là TEACHER, ngược lại là false.
     */
    public static boolean isTeacher() {
        return hasRole("TEACHER");
    }

    /**
     * Kiểm tra xem người dùng hiện tại có phải là STUDENT hay không.
     *
     * @return True nếu là STUDENT, ngược lại là false.
     */
    public static boolean isStudent() {
        return hasRole("STUDENT");
    }
}
