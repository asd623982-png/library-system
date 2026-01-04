package com.library.system.service;

import com.library.system.entity.Book;
import com.library.system.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    // 查询所有图书
    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    // 图书入库/修改
    public Book saveBook(Book book) {
        // 这里可以加逻辑：比如判断 ISBN 是否已存在
        return bookRepository.save(book);
    }

    // 根据ID查书
    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }
}