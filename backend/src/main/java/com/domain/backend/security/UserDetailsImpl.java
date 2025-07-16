package com.domain.backend.security;

import com.domain.backend.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Triển khai tùy chỉnh của interface UserDetails của Spring Security.
 * Lớp này bọc mô hình User của chúng ta để cung cấp thông tin xác thực và ủy quyền.
 */
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private String id;

    private String username;

    private String email;

    @JsonIgnore // Ngăn không cho mật khẩu được tuần tự hóa thành JSON
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(String id, String username, String email, String password,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    /**
     * Xây dựng đối tượng UserDetailsImpl từ một mô hình User.
     *
     * @param user Mô hình User.
     * @return Thể hiện UserDetailsImpl.
     */
    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(SimpleGrantedAuthority::new) // Sử dụng constructor của SimpleGrantedAuthority
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPasswordHash(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Tài khoản không bao giờ hết hạn
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Tài khoản không bao giờ bị khóa
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Thông tin đăng nhập không bao giờ hết hạn
    }

    @Override
    public boolean isEnabled() {
        return true; // Tài khoản luôn được kích hoạt
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}