package com.library.system.repository;

import com.library.system.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    // 基础查询
    List<Book> findByTitleContaining(String title);

    // ★ 新增：统计每个分类下的图书数量
    // 结果格式：Object[] { "计算机", 120 }
    @Query("SELECT b.category, COUNT(b) FROM Book b GROUP BY b.category")
    List<Object[]> countBooksByCategory();
}