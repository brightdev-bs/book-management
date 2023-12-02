package com.example.bookmanagement.controller;

import com.example.bookmanagement.global.payload.book.*;
import com.example.bookmanagement.global.payload.response.ApiResponse;
import com.example.bookmanagement.service.BookService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/return")
    public ApiResponse returnBook(@RequestBody BookReturnForm form) {
        BookReturnReceipt response = bookService.returnBook(form);
        return ApiResponse.of(HttpStatus.OK.toString(), response);
    }

    @PostMapping("/register")
    public ApiResponse registerBook(@RequestBody BookRegisterFrom form) {
        BookDetails response = bookService.registerBook(form);
        return ApiResponse.of(HttpStatus.CREATED.toString(), response);
    }

    @PatchMapping("/update")
    public ApiResponse updateBook(@RequestBody BookUpdateForm form) {
        BookDetails response = bookService.updateBook(form);
        return ApiResponse.of(HttpStatus.OK.toString(), response);
    }

    @GetMapping("/{bookId}/history")
    public ApiResponse searchHistory(@PathVariable(name = "bookId") Long bookId) {
        BookAndHistoryDetail response = bookService.searchBookHistory(bookId);
        return ApiResponse.of(HttpStatus.OK.toString(), response);
    }
}
