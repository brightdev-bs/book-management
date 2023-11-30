package com.example.bookmanagement.service;

import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.entity.BookHistory;
import com.example.bookmanagement.entity.Member;
import com.example.bookmanagement.global.exception.BookNotAvailableException;
import com.example.bookmanagement.global.exception.NotFoundException;
import com.example.bookmanagement.global.payload.book.BookBorrowForm;
import com.example.bookmanagement.global.payload.book.BookReturnForm;
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

import java.time.LocalDate;
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

        given(memberRepository.findById(any(UUID.class))).willReturn(Optional.of(member));
        given(bookRepository.findById(any(Long.class))).willReturn(Optional.of(book));

        bookService.borrowBook(form);

        then(book).should().setBorrowed(true);
        then(bookHistoryRepository).should().save(any(BookHistory.class));
    }

    @DisplayName("도서 대출 실패: 회원 아이디 존재하지 않음")
    @Test
    void borrowBookFailedWithNotFoundMemberId() {
        given(memberRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> bookService.borrowBook(this.getForm()));
    }

    @DisplayName("도서 대출 실패: 책 이용 불가능")
    @Test
    void borrowBookFailedWithNotAvailable() {
        BookBorrowForm form = this.getForm();
        Member member = mock(Member.class);
        Book book = mock(Book.class);

        given(memberRepository.findById(any(UUID.class))).willReturn(Optional.of(member));
        given(bookRepository.findById(any(Long.class))).willReturn(Optional.of(book));
        given(book.getBorrowed()).willReturn(true);

        Assertions.assertThrows(BookNotAvailableException.class, () -> bookService.borrowBook(form));
    }

    @DisplayName("도서 반납")
    @Test
    void returnBook() {
        BookReturnForm form = getBookReturnForm();
        BookHistory history = mock(BookHistory.class);
        Book book = mock(Book.class);

        given(bookHistoryRepository.findById(any(Long.class))).willReturn(Optional.of(history));
        given(history.getBook()).willReturn(book);
        given(book.getName()).willReturn("test");

        bookService.returnBook(form);

        then(bookHistoryRepository).should().findById(any(Long.class));
        then(history).should().setReturnDate(LocalDate.now());
        then(book).should().setBorrowed(false);
    }

    @DisplayName("도서 반납 실패: 이력 없음")
    @Test
    void returnBookFailedWithNotFoundHistory() {
        given(bookHistoryRepository.findById(any(Long.class))).willReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> bookService.returnBook(getBookReturnForm()));
    }

    private BookBorrowForm getForm() {
        return new BookBorrowForm(
                UUID.randomUUID(),
                1L
        );
    }

    private BookReturnForm getBookReturnForm() {
        return new BookReturnForm(1L);
    }
}