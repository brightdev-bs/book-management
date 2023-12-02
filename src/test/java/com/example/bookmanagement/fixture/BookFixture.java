package com.example.bookmanagement.fixture;

import com.example.bookmanagement.entity.Book;

public class BookFixture {

    public static Book getDefaultBook() {
        return Book.builder()
                .id(1L)
                .name("test")
                .author("tester")
                .borrowed(false)
                .build();
    }

    public static Book getBookWithBookName(String name) {
        return Book.builder()
                .name(name)
                .author("tester")
                .borrowed(false)
                .build();
    }
}
