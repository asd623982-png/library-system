package com.library.system.repository;

import com.library.system.entity.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface BorrowRepository extends JpaRepository<Borrow, Long> {

    // 基础查询
    List<Borrow> findByUserId(Long userId);

    // 流量统计
    @Query(value = "SELECT DATE_FORMAT(borrow_time, '%Y-%m-%d') as dateStr, COUNT(*) as cnt " +
            "FROM sys_borrow " +
            "GROUP BY dateStr " +
            "ORDER BY dateStr ASC LIMIT 30", nativeQuery = true)
    List<Object[]> findTrafficData();

    // ★★★ 核心修改：在 SELECT 最前面增加了 r.id ★★★
    @Query(value = "SELECT r.id, b.title, b.isbn, r.borrow_time " +
            "FROM sys_borrow r " +
            "LEFT JOIN sys_book b ON r.book_id = b.id " +
            "WHERE r.user_id = ?1 AND r.status = 1", nativeQuery = true)
    List<Object[]> findUserCurrentBorrows(Long userId);
}