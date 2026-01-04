package com.library.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sys_borrow")
public class Borrow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId; // 对应 User 的 id

    @Column(name = "book_id", nullable = false)
    private Long bookId; // 对应 Book 的 id

    @Column(name = "borrow_time")
    private LocalDateTime borrowTime;

    @Column(name = "due_date")
    private LocalDateTime dueDate; // 应还时间

    @Column(name = "return_time")
    private LocalDateTime returnTime; // 实际归还时间

    private Integer status; // 1:借出 2:归还 3:逾期
}