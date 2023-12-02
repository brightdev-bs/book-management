package com.example.bookmanagement.entity;

import com.example.bookmanagement.global.payload.book.BookUpdateForm;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@Entity
public class Book extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String author;

    private Boolean borrowed;

    @OneToMany(mappedBy = "book")
    private List<BookHistory> histories = new ArrayList<>();

    public Book() {}

    @Builder
    public Book(Long id, String name, String author, Boolean borrowed, List<BookHistory> histories) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.borrowed = borrowed;
        this.histories = histories == null ? new ArrayList<>() : histories;
    }

    public void setBorrowed(boolean flag) {
        this.borrowed = flag;
    }

    public void updateInfo(BookUpdateForm form) {
        this.author = form.author();
        this.name = form.bookName();
    }
}
