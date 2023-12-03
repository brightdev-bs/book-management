package com.example.bookmanagement.fixture;

import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.entity.BookHistory;
import com.example.bookmanagement.entity.Member;

import java.time.LocalDate;
import java.util.UUID;

public class BookHistoryFixture {

    public static BookHistory getDefaultHistory(Book book, Member member) {
        return BookHistory.builder()
                .book(book)
                .memberId(member.getId())
                .build();
    }

    public static BookHistory generateCompleteHistory(Book book) {
        BookHistory history = BookHistory.builder()
                .book(book)
                .memberId(UUID.randomUUID())
                .borrowedAt(LocalDate.now())
                .returnedAt(LocalDate.now())
                .build();
        book.getHistories().add(history);
        return history;
    }
}
