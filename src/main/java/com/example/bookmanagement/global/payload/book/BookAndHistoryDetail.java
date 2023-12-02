package com.example.bookmanagement.global.payload.book;

import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.entity.BookHistory;

import java.util.List;
import java.util.UUID;

public record BookAndHistoryDetail(
        Long bookId,
        String bookName,
        String author,
        List<BookHistoryDetail> histories
) {

    public static BookAndHistoryDetail from(Book book) {
        List<BookHistory> historyList = book.getHistories();
        List<BookHistoryDetail> historyDetails = historyList.stream()
                .map(BookHistoryDetail::from)
                .toList();

        return new BookAndHistoryDetail(
                book.getId(),
                book.getName(),
                book.getAuthor(),
                historyDetails
        );
    }
}
