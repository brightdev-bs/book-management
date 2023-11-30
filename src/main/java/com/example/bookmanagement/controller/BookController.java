package com.example.bookmanagement.controller;

import com.example.bookmanagement.global.payload.book.BookBorrowForm;
import com.example.bookmanagement.global.payload.book.BookBorrowReceipt;
import com.example.bookmanagement.global.payload.response.ApiResponse;
import com.example.bookmanagement.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/books")
@RestController
public class BookController {

    private final BookService bookService;

    @PostMapping("/borrow")
    public ApiResponse borrowBook(@RequestBody BookBorrowForm form) {
        BookBorrowReceipt response = bookService.borrowBook(form);
        return ApiResponse.of(HttpStatus.OK.toString(), response);
    }
}
