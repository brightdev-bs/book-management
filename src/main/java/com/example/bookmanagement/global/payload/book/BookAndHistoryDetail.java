package com.example.bookmanagement.global.payload.book;

import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.entity.BookHistory;
import org.springframework.data.domain.Page;

import java.util.List;

public record BookAndHistoryDetail(
        Long bookId,
        String bookName,
        String author,
        List<BookHistoryDetail> histories,
        int page,
        int size
) {

    public static BookAndHistoryDetail from(Page<BookHistory> bookHistories) {
        List<BookHistory> contents = bookHistories.getContent();
        Book book = contents.get(0).getBook();
        List<BookHistoryDetail> historyDetails = contents.stream().map(BookHistoryDetail::from).toList();
        return new BookAndHistoryDetail(
                book.getId(),
                book.getName(),
                book.getAuthor(),
                historyDetails,
                bookHistories.getNumber(),
                bookHistories.getSize()
        );
    }
}
