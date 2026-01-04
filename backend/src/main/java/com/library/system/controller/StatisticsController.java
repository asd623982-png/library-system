package com.library.system.controller;

import com.library.system.repository.BookRepository;
import com.library.system.repository.BorrowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin
public class StatisticsController {

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private BookRepository bookRepository; // 注入 BookRepository

    // 1. 流量接口 (保持不变)
    @GetMapping("/traffic")
    public List<Map<String, Object>> getTraffic() {
        List<Object[]> results = borrowRepository.findTrafficData();
        List<Map<String, Object>> chartData = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", row[0].toString());
            item.put("count", row[1]);
            chartData.add(item);
        }
        return chartData;
    }

    // 2. ★ 新增：图书分类统计接口
    @GetMapping("/categories")
    public List<Map<String, Object>> getCategoryStats() {
        List<Object[]> results = bookRepository.countBooksByCategory();
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", row[0]); // 分类名
            item.put("value", row[1]); // 数量
            list.add(item);
        }
        return list;
    }
}