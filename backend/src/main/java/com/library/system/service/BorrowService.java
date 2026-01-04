package com.library.system.service;

import com.library.system.entity.Book;
import com.library.system.entity.Borrow;
import com.library.system.repository.BookRepository;
import com.library.system.repository.BorrowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class BorrowService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowRepository borrowRepository;

    @Transactional
    public String borrowBook(Long userId, Long bookId) {
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) return "图书不存在";
        if (book.getStock() <= 0) return "库存不足";

        // 扣减库存
        book.setStock(book.getStock() - 1);
        bookRepository.save(book);

        // 创建借阅记录
        Borrow borrow = new Borrow();
        borrow.setUserId(userId);
        borrow.setBookId(bookId);
        borrow.setBorrowTime(LocalDateTime.now());
        borrow.setDueDate(LocalDateTime.now().plusDays(30));
        borrow.setStatus(1); // 1-借出
        borrowRepository.save(borrow);

        return "借阅成功";
    }

    // ★★★ 新增：还书逻辑 ★★★
    @Transactional
    public String returnBook(Long borrowId) {
        Borrow borrow = borrowRepository.findById(borrowId).orElse(null);
        if (borrow == null) return "记录不存在";
        if (borrow.getStatus() == 2) return "该书已归还";

        // 1. 更新借阅状态
        borrow.setStatus(2); // 2-已还
        borrow.setReturnTime(LocalDateTime.now());
        borrowRepository.save(borrow);

        // 2. 恢复图书库存
        Book book = bookRepository.findById(borrow.getBookId()).orElse(null);
        if (book != null) {
            book.setStock(book.getStock() + 1);
            bookRepository.save(book);
        }

        return "还书成功";
    }
}