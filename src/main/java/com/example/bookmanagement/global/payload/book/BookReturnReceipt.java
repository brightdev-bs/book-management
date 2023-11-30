package com.example.bookmanagement.global.payload.book;

import com.example.bookmanagement.entity.BookHistory;

import java.time.LocalDate;

public record BookReturnReceipt(
        String bookName,
        LocalDate borrowedAt,
        LocalDate returnedAt
) {

    public static BookReturnReceipt from(BookHistory bookHistory) {
        return new BookReturnReceipt(
                bookHistory.getBook().getName(),
                bookHistory.getBorrowedAt(),
                LocalDate.now());
    }
}
