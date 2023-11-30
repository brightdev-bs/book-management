package com.example.bookmanagement.repository;

import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.entity.BookHistory;
import com.example.bookmanagement.entity.Member;
import com.example.bookmanagement.fixture.BookFixture;
import com.example.bookmanagement.fixture.MemberFixture;
import com.example.bookmanagement.global.config.JpaConfig;
import com.example.bookmanagement.global.constants.ErrorCode;
import com.example.bookmanagement.global.exception.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;

@Import(JpaConfig.class)
@DataJpaTest
class BookHistoryRepositoryTest {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BookHistoryRepository bookHistoryRepository;


    @DisplayName("이력이 여러개 있을 때 returnedAt이 null인 값을 찾아온다.")
    @Test
    void findByBookAndReturnedAtIsEmpty() {
        Member member = memberRepository.save(MemberFixture.getDefaultMember());
        Book book = bookRepository.save(BookFixture.getBookWithBookName("book1"));

        BookHistory history = BookHistory.builder()
                .book(book)
                .memberId(member.getId())
                .returnedAt(LocalDate.now())
                .build();
        bookHistoryRepository.save(history);

        BookHistory history2 = BookHistory.builder()
                .book(book)
                .memberId(member.getId())
                .returnedAt(LocalDate.now())
                .build();
        bookHistoryRepository.save(history2);

        BookHistory history3 = BookHistory.builder()
                .book(book)
                .memberId(member.getId())
                .build();
        bookHistoryRepository.save(history3);

        BookHistory bookHistory = bookHistoryRepository.findByBookAndReturnedAtNull(book)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_BOOK_HISTORY));
        Assertions.assertEquals(bookHistory.getId(), history3.getId());
    }

}