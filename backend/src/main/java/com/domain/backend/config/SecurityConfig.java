package com.domain.backend.config;

import com.domain.backend.security.CustomUserDetailsService;
import com.domain.backend.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationEntryPoint unauthorizedHandler; // Sử dụng AuthenticationEntryPoint trực tiếp

    /**
     * Tạo và trả về một thể hiện của JwtAuthenticationFilter.
     * Bộ lọc này sẽ được sử dụng để xử lý JWT trong các yêu cầu đến.
     *
     * @return Thể hiện JwtAuthenticationFilter.
     */
    @Bean
    public JwtAuthenticationFilter authenticationJwtTokenFilter() {
        return new JwtAuthenticationFilter();
    }

    /**
     * Cấu hình DaoAuthenticationProvider, sử dụng CustomUserDetailsService
     * và PasswordEncoder để xác thực người dùng.
     *
     * @return Thể hiện DaoAuthenticationProvider.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // Đặt CustomUserDetailsService tùy chỉnh
        authProvider.setPasswordEncoder(passwordEncoder()); // Đặt bộ mã hóa mật khẩu
        return authProvider;
    }

    /**
     * Cung cấp bean AuthenticationManager, được sử dụng để xác thực người dùng.
     *
     * @param authConfig Cấu hình xác thực
     * @return Thể hiện AuthenticationManager.
     * @throws Exception nếu xảy ra lỗi trong quá trình cấu hình.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Cung cấp bean PasswordEncoder, sử dụng BCrypt để băm mật khẩu mạnh.
     *
     * @return Thể hiện PasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Cấu hình SecurityFilterChain, định nghĩa các quy tắc bảo mật cho các yêu cầu HTTP.
     *
     * @param http Đối tượng HttpSecurity.
     * @return Thể hiện SecurityFilterChain.
     * @throws Exception nếu xảy ra lỗi trong quá trình cấu hình.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // Tắt CSRF vì JWT là stateless
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler)) // Xử lý truy cập trái phép
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Sử dụng phiên stateless cho JWT
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/auth/**").permitAll() // Cho phép truy cập công khai vào các endpoint xác thực
                                .requestMatchers("/api/test/**").permitAll() // Ví dụ: cho phép truy cập công khai vào các endpoint kiểm thử
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Cho phép truy cập Swagger UI
                                .anyRequest().authenticated() // Tất cả các yêu cầu khác đều yêu cầu xác thực
                );

        http.authenticationProvider(authenticationProvider()); // Đặt nhà cung cấp xác thực

        // Thêm bộ lọc JWT token trước UsernamePasswordAuthenticationFilter
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
