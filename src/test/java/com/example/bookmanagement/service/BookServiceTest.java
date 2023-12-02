package com.example.bookmanagement.service;

import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.entity.BookHistory;
import com.example.bookmanagement.entity.Member;
import com.example.bookmanagement.fixture.BookFixture;
import com.example.bookmanagement.global.exception.BookNotAvailableException;
import com.example.bookmanagement.global.exception.DelayedMemberException;
import com.example.bookmanagement.global.exception.NotFoundException;
import com.example.bookmanagement.global.payload.book.*;
import com.example.bookmanagement.repository.BookCacheRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@Transactional
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    BookRepository bookRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    BookCacheRepository bookCacheRepository;

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
        given(member.getId()).willReturn(UUID.randomUUID());
        given(bookCacheRepository.isDelayedMember(any(UUID.class))).willReturn(false);

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

    @DisplayName("도서 대출 실패: 연체자")
    @Test
    void borrowBookFailedWithBlocked() {
        BookBorrowForm form = this.getForm();
        Member member = mock(Member.class);
        Book book = mock(Book.class);

        given(memberRepository.findById(any(UUID.class))).willReturn(Optional.of(member));
        given(bookRepository.findById(any(Long.class))).willReturn(Optional.of(book));
        given(member.getId()).willReturn(UUID.randomUUID());
        given(bookCacheRepository.isDelayedMember(any(UUID.class))).willReturn(true);

        Assertions.assertThrows(DelayedMemberException.class, () -> bookService.borrowBook(form));

    }

    @DisplayName("도서 반납")
    @Test
    void returnBook() {
        BookReturnForm form = getBookReturnForm();
        BookHistory history = mock(BookHistory.class);
        Book book = mock(Book.class);

        given(bookRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(book));
        given(bookHistoryRepository.findByBookAndReturnedAtNull(any(Book.class))).willReturn(Optional.of(history));
        given(history.getBook()).willReturn(book);
        given(book.getName()).willReturn("test");
        given(history.getBorrowedAt()).willReturn(LocalDate.of(2023, 11, 24));
        given(history.getReturnedAt()).willReturn(LocalDate.of(2023, 11, 30));

        bookService.returnBook(form);

        then(bookHistoryRepository).should().findByBookAndReturnedAtNull(any(Book.class));
        then(history).should().setReturnDate(LocalDate.now());
        then(book).should().setBorrowed(false);
    }

    @DisplayName("도서 반납 실패: 이력 없음")
    @Test
    void returnBookFailedWithNotFoundHistory() {
        Book book = mock(Book.class);
        given(bookRepository.findById(any(Long.class))).willReturn(Optional.of(book));

        Assertions.assertThrows(NotFoundException.class, () -> bookService.returnBook(getBookReturnForm()));
    }


    // 2023/11/23 -> 2023/11/30은 연체 아님
    @DisplayName("연체시 3일 동안 대출을 못하게 이력을 남긴다.")
    @Test
    void checkIfDelayed() {
        Book book = mock(Book.class);
        BookHistory bookHistory = mock(BookHistory.class);
        given(bookRepository.findById(any(Long.class))).willReturn(Optional.of(book));
        given(bookHistoryRepository.findByBookAndReturnedAtNull(any(Book.class))).willReturn(Optional.of(bookHistory));
        given(bookHistory.getBorrowedAt()).willReturn(LocalDate.of(2023, 11, 22));
        given(bookHistory.getReturnedAt()).willReturn(LocalDate.of(2023, 11, 30));
        given(bookHistory.getBook()).willReturn(book);
        given(bookHistory.getMemberId()).willReturn(UUID.randomUUID());


        bookService.returnBook(this.getBookReturnForm());

        then(bookCacheRepository).should().setDelayedMember(any(UUID.class));
    }

    private BookBorrowForm getForm() {
        return new BookBorrowForm(
                UUID.randomUUID(),
                1L
        );
    }

    @DisplayName("책 등록")
    @Test
    void registerBook() {
        BookRegisterFrom form = mock(BookRegisterFrom.class);

        bookService.registerBook(form);

        then(bookRepository).should().save(any(Book.class));
    }

    @DisplayName("책 수정")
    @Test
    void updateBook() {
        BookUpdateForm form = getUpdateForm();
        given(bookRepository.findById(any(Long.class))).willReturn(Optional.of(BookFixture.getDefaultBook()));

        BookDetails bookDetails = bookService.updateBook(form);

        Assertions.assertEquals(bookDetails.bookName(), form.bookName());
        Assertions.assertEquals(bookDetails.author(), form.author());
    }

    @DisplayName("책 수정 실패: 책 정보 찾을 수 없음")
    @Test
    void updateBookFail() {
        BookUpdateForm form = getUpdateForm();
        Assertions.assertThrows(NotFoundException.class, () -> bookService.updateBook(form));
    }

    @DisplayName("책 이력 조회")
    @Test
    void searchBookHistory() {
        Book book = mock(Book.class);
        given(bookRepository.findById(any(Long.class))).willReturn(Optional.of(book));
        given(book.getHistories()).willReturn(new ArrayList<>());

        BookAndHistoryDetail result = bookService.searchBookHistory(any(Long.class));

        assertNotNull(result);
    }

    @DisplayName("책 이력 조회 실패")
    @Test
    void searchBookHistoryFailed() {
        Assertions.assertThrows(NotFoundException.class, () -> bookService.searchBookHistory(any(Long.class)));
    }

    private BookUpdateForm getUpdateForm() {
        return new BookUpdateForm(1L, "changedName", "changedAuthor");
    }

    private BookReturnForm getBookReturnForm() {
        return new BookReturnForm(1L);
    }
}