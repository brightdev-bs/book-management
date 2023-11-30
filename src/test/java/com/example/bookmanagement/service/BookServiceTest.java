package com.example.bookmanagement.service;

import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.entity.BookHistory;
import com.example.bookmanagement.entity.Member;
import com.example.bookmanagement.global.exception.BookNotAvailableException;
import com.example.bookmanagement.global.exception.NotFoundException;
import com.example.bookmanagement.global.payload.book.BookBorrowForm;
import com.example.bookmanagement.repository.BookHistoryRepository;
import com.example.bookmanagement.repository.BookRepository;
import com.example.bookmanagement.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    BookRepository bookRepository;
    @Mock
    MemberRepository memberRepository;

    @Mock
    BookHistoryRepository bookHistoryRepository;

    @InjectMocks
    BookService bookService;

    @DisplayName("도서 대출")
    @Test
    void borrowBook() {
        BookBorrowForm form = this.getForm();
        Member member = mock(Member.class);
        Book book = mock(Book.class);

        given(memberRepository.findByUUID(any(UUID.class))).willReturn(Optional.of(member));
        given(bookRepository.findById(any(Long.class))).willReturn(Optional.of(book));

        bookService.borrowBook(form);

        then(book).should().setIsBorrowed(true);
        then(bookHistoryRepository).should().save(any(BookHistory.class));
    }

    @DisplayName("도서 대출 실패: 회원 아이디 존재하지 않음")
    @Test
    void borrowBookFailedWithNotFoundMemberId() {
        given(memberRepository.findByUUID(any(UUID.class))).willReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> bookService.borrowBook(this.getForm()));
    }

    @DisplayName("도서 대출 실패: 책 이용 불가능")
    @Test
    void borrowBookFailedWithNotAvailable() {
        BookBorrowForm form = this.getForm();
        Member member = mock(Member.class);
        Book book = mock(Book.class);

        given(memberRepository.findByUUID(any(UUID.class))).willReturn(Optional.of(member));
        given(bookRepository.findById(any(Long.class))).willReturn(Optional.of(book));
        given(book.getIsBorrowed()).willReturn(true);

        Assertions.assertThrows(BookNotAvailableException.class, () -> bookService.borrowBook(form));
    }

    private BookBorrowForm getForm() {
        return new BookBorrowForm(
                UUID.randomUUID(),
                1L
        );
    }
}