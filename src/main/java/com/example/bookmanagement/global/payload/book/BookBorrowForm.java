package com.example.bookmanagement.global.payload.book;

import java.util.UUID;

public record BookBorrowForm(
        UUID memberId,
        Long bookId
) {
}
