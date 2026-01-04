package com.library.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data // Lombok注解：自动生成get/set/toString等方法
@Entity // 告诉JPA这是一个实体类
@Table(name = "sys_user") // 对应数据库的 sys_user 表
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String role; // ADMIN 或 USER

    private Integer status; // 1:正常 0:禁用

    @Column(name = "create_time")
    private LocalDateTime createTime;

    // 在保存前自动填充创建时间
    @PrePersist
    public void prePersist() {
        if (createTime == null) createTime = LocalDateTime.now();
    }
}