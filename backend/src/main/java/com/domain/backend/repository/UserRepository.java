package com.domain.backend.repository;

import com.domain.backend.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Đánh dấu interface này là một Spring Data repository
public interface UserRepository extends MongoRepository<User, String> {

    // Phương thức tùy chỉnh để tìm User theo địa chỉ email
    Optional<User> findByEmail(String email);

    // Phương thức tùy chỉnh để kiểm tra xem một user với email đã cho có tồn tại không
    Boolean existsByEmail(String email);

}
