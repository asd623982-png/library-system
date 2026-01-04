package com.library.system.repository;

import com.library.system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// 继承 JpaRepository，自动获得 CRUD 功能
public interface UserRepository extends JpaRepository<User, Long> {

    // 魔法方法：根据用户名查找用户
    Optional<User> findByUsername(String username);
}