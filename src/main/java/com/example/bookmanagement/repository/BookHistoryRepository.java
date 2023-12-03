package com.example.bookmanagement.repository;

import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.entity.BookHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookHistoryRepository extends JpaRepository<BookHistory, Long> {
    Optional<BookHistory> findByBookAndReturnedAtNull(Book book);

    Page<BookHistory> findAllByBook(Book book, Pageable pageable);
}
