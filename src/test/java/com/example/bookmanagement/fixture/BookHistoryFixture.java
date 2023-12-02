package com.example.bookmanagement.fixture;

import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.entity.BookHistory;
import com.example.bookmanagement.entity.Member;

import java.time.LocalDate;

public class BookHistoryFixture {

    public static BookHistory getDefaultHistory(Book book, Member member) {
        return BookHistory.builder()
                .book(book)
                .memberId(member.getId())
                .build();
    }
}
