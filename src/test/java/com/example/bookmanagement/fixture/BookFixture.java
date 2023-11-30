package com.example.bookmanagement.fixture;

import com.example.bookmanagement.entity.Book;

import java.time.LocalDate;

public class BookFixture {

    public static Book getDefaultBook() {
        return Book.builder()
                .name("test")
                .author("tester")
                .build();
    }
}
