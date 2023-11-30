package com.example.bookmanagement.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@Entity
public class Book extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String author;

    private Boolean isAvailable;

    @OneToMany(mappedBy = "book")
    private List<BookHistory> histories;

    public Book() {}

    @Builder
    public Book(Long id, String name, String author, Boolean isAvailable, List<BookHistory> histories) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.isAvailable = isAvailable;
        this.histories = histories;
    }
}
