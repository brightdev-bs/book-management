package com.example.bookmanagement.global.payload.book;

import com.example.bookmanagement.entity.BookHistory;

import java.time.LocalDate;
import java.util.UUID;

public record BookHistoryDetail(
        Long historyId,
        UUID memberId,
        LocalDate borrwedAt,
        LocalDate returnedAt
) {

    public static BookHistoryDetail from(BookHistory history) {
        return new BookHistoryDetail(
                history.getId(),
                history.getMemberId(),
                history.getBorrowedAt(),
                history.getReturnedAt()
        );
    }
}
