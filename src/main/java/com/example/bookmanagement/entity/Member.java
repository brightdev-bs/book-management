package com.example.bookmanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.UUIDGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Member {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", type = UUIDGenerator.class)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    private String name;

    @Column(unique = true)
    private String email;

    @CreatedDate
    private LocalDate createdAt;

    public Member() {}

    private Member(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public static Member of(String name, String email) {
        return new Member(name, email);
    }
}
