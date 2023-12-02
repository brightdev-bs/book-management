package com.example.bookmanagement.global.payload.book;

import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.entity.BookHistory;
import com.example.bookmanagement.entity.Member;

import java.time.LocalDate;

public record BookBorrowReceipt(
        Long bookId,
        String bookName,
        String borrowedBy,
        LocalDate borrowedAt
) {

    public static BookBorrowReceipt from(Book book, Member member) {
        return new BookBorrowReceipt(
                book.getId(),
                book.getName(),
                member.getName(),
                LocalDate.now()
            );
    }
}
