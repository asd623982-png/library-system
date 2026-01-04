package com.library.system.controller;

import com.library.system.entity.Book;
import com.library.system.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // 告诉Spring这是一个写接口的类
@RequestMapping("/api/books") // 所有接口都以 /api/books 开头
@CrossOrigin // 允许跨域（关键！让前端Vue能访问后端）
public class BookController {

    @Autowired
    private BookService bookService;

    // 获取图书列表
    // GET http://localhost:8080/api/books
    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.findAllBooks();
    }

    // 添加图书
    // POST http://localhost:8080/api/books
    @PostMapping
    public Book addBook(@RequestBody Book book) {
        return bookService.saveBook(book);
    }
}