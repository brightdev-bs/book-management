package com.example.bookmanagement.service;

import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.entity.BookHistory;
import com.example.bookmanagement.entity.Member;
import com.example.bookmanagement.global.constants.ErrorCode;
import com.example.bookmanagement.global.exception.BookNotAvailableException;
import com.example.bookmanagement.global.exception.DelayedMemberException;
import com.example.bookmanagement.global.exception.NotFoundException;
import com.example.bookmanagement.global.payload.book.BookBorrowForm;
import com.example.bookmanagement.global.payload.book.BookBorrowReceipt;
import com.example.bookmanagement.global.payload.book.BookReturnForm;
import com.example.bookmanagement.global.payload.book.BookReturnReceipt;
import com.example.bookmanagement.repository.BookCacheRepository;
import com.example.bookmanagement.repository.BookHistoryRepository;
import com.example.bookmanagement.repository.BookRepository;
import com.example.bookmanagement.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BookService {

    private final BookHistoryRepository bookHistoryRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final BookCacheRepository bookCacheRepository;


    @Transactional
    public BookBorrowReceipt borrowBook(BookBorrowForm form) {
        Member member = memberRepository.findById(form.memberId()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MEMBER));
        Book book = bookRepository.findById(form.bookId()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_BOOK));

        if (book.getBorrowed()) {
            throw new BookNotAvailableException(ErrorCode.NOT_AVAILABLE_BOOK);
        }

        if (bookCacheRepository.isDelayedMember(member.getId())) {
            throw new DelayedMemberException(ErrorCode.DELAYED_USER);
        }

        recordHistory(book, member);
        return BookBorrowReceipt.from(book, member);
    }

    private BookHistory recordHistory(Book book, Member member) {
        book.setBorrowed(true);

        BookHistory history = BookHistory.builder()
                .book(book)
                .memberId(member.getId())
                .borrowedAt(LocalDate.now())
                .build();

        return bookHistoryRepository.save(history);
    }

    @Transactional
    public BookReturnReceipt returnBook(BookReturnForm bookReturnForm) {
        Book book = bookRepository.findById(bookReturnForm.bookId()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_BOOK));
        BookHistory bookHistory = bookHistoryRepository.findByBookAndReturnedAtNull(book).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_BOOK_HISTORY));
        bookHistory.setReturnDate(LocalDate.now());

        if (isDelayed(bookHistory)) {
            bookCacheRepository.setDelayedMember(bookHistory.getMemberId());
        }

        bookHistory.getBook().setBorrowed(false);
        return BookReturnReceipt.from(bookHistory);
    }

    private boolean isDelayed(BookHistory history) {
        if (history.getBorrowedAt().plusDays(7L).isBefore(history.getReturnedAt()))
            return true;
        return false;
    }
}
