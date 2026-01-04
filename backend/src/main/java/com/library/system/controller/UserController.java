package com.library.system.controller;

import com.library.system.entity.User;
import com.library.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // ★★★ 核心修改：只允许管理员登录 ★★★
    @PostMapping("/login")
    public User login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("账号不存在"));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("密码错误");
        }

        // 关键点：如果你不是 ADMIN，不让你进！
        if (!"ADMIN".equals(user.getRole())) {
            throw new RuntimeException("权限不足：该系统仅限管理员访问");
        }

        return user;
    }

    // 添加读者 (管理员用的功能)
    @PostMapping("/add")
    public User addUser(@RequestBody User user) {
        user.setPassword("123456"); // 默认密码
        user.setRole("USER");       // 强制设为读者
        user.setStatus(1);
        return userRepository.save(user);
    }

    // 获取所有用户(包括管理员和读者)
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 修改密码
    @PostMapping("/change-password")
    public String changePassword(@RequestBody Map<String, Object> data) {
        Long userId = Long.valueOf(data.get("userId").toString());
        String newPass = data.get("newPassword").toString();
        User user = userRepository.findById(userId).get();
        user.setPassword(newPass);
        userRepository.save(user);
        return "修改成功";
    }
}