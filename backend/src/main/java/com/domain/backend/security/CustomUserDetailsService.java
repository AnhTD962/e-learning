package com.domain.backend.security;

import com.domain.backend.entity.User;
import com.domain.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Triển khai tùy chỉnh của interface UserDetailsService của Spring Security.
 * Lớp này chịu trách nhiệm tải dữ liệu người dùng cụ thể trong quá trình xác thực.
 */
@Service // Đánh dấu lớp này là một Spring Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Tải chi tiết người dùng theo tên người dùng (trong trường hợp này là email).
     *
     * @param email Email của người dùng cần tải.
     * @return Đối tượng UserDetails.
     * @throws UsernameNotFoundException nếu không tìm thấy người dùng với email đã cho.
     */
    @Override
    @Transactional // Đảm bảo toàn bộ phương thức thực thi trong một giao dịch duy nhất
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Tìm người dùng theo email, ném ngoại lệ nếu không tìm thấy
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email));

        // Xây dựng UserDetailsImpl từ User tìm được
        return UserDetailsImpl.build(user);
    }
}