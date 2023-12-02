package com.example.bookmanagement.global.payload.book;

public record BookUpdateForm(
        Long id,
        String bookName,
        String author
) {
}
