package com.example.bookmanagement.repository;

import com.example.bookmanagement.entity.BookHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookHistoryRepository extends JpaRepository<BookHistory, Long> {
}
