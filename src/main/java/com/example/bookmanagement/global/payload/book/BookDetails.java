package com.example.bookmanagement.global.payload.book;

import com.example.bookmanagement.entity.Book;

import java.time.LocalDate;

public record BookDetails(
        Long id,
        String bookName,
        String author,
        LocalDate createdAt,
        LocalDate updatedAt
) {

    public static BookDetails from(Book book) {
        return new BookDetails(
                book.getId(),
                book.getName(),
                book.getAuthor(),
                book.getCreatedAt(),
                book.getUpdatedAt()
        );
    }
}
