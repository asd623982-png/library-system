package com.library.system.controller;

import com.library.system.repository.BorrowRepository;
import com.library.system.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/borrow")
@CrossOrigin
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    @Autowired
    private BorrowRepository borrowRepository;

    // 借书
    @PostMapping
    public String borrow(@RequestParam Long userId, @RequestParam Long bookId) {
        return borrowService.borrowBook(userId, bookId);
    }

    // ★★★ 新增：还书接口 ★★★
    @PostMapping("/return")
    public String returnBook(@RequestParam Long borrowId) {
        return borrowService.returnBook(borrowId);
    }

    // 查询详情
    @GetMapping("/user/{userId}")
    public List<Map<String, Object>> getUserBorrows(@PathVariable Long userId) {
        List<Object[]> list = borrowRepository.findUserCurrentBorrows(userId);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("borrowId", row[0]);    // ★ 关键：把借阅ID传回去
            map.put("bookTitle", row[1]);
            map.put("isbn", row[2]);
            map.put("borrowTime", row[3]);
            result.add(map);
        }
        return result;
    }
}