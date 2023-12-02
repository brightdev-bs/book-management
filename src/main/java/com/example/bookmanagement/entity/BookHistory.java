package com.example.bookmanagement.entity;

import com.example.bookmanagement.global.utils.DateUtils;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@ToString
@EntityListeners(AuditingEntityListener.class)
@Entity
public class BookHistory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    private LocalDate borrowedAt;

    @CreatedBy
    private UUID memberId;

    private LocalDate returnedAt;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    public BookHistory() {}

    public void setReturnDate(LocalDate date) {
        this.returnedAt = date;
    }

    @Builder
    public BookHistory(Long id, LocalDate borrowedAt, UUID memberId, LocalDate returnedAt, Book book) {
        this.id = id;
        this.borrowedAt = borrowedAt;
        this.memberId = memberId;
        this.returnedAt = returnedAt;
        this.book = book;
    }
}
