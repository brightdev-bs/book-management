package com.example.bookmanagement.service;

import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.entity.BookHistory;
import com.example.bookmanagement.entity.Member;
import com.example.bookmanagement.global.constants.ErrorCode;
import com.example.bookmanagement.global.exception.BookNotAvailableException;
import com.example.bookmanagement.global.exception.DelayedMemberException;
import com.example.bookmanagement.global.exception.NotFoundException;
import com.example.bookmanagement.global.payload.book.*;
import com.example.bookmanagement.repository.BookCacheRepository;
import com.example.bookmanagement.repository.BookHistoryRepository;
import com.example.bookmanagement.repository.BookRepository;
import com.example.bookmanagement.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

        if (book.isBorrowed()) {
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

    @Transactional
    public BookDetails registerBook(BookRegisterFrom form) {
        Book book = Book.builder()
                .name(form.bookName())
                .author(form.author())
                .histories(new ArrayList<>())
                .build();
        bookRepository.save(book);
        return BookDetails.from(book);
    }

    @Transactional
    public BookDetails updateBook(BookUpdateForm form) {
        Book book = bookRepository.findById(form.id()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_BOOK));
        book.updateInfo(form);
        return BookDetails.from(book);
    }

    // 배치 쿼리 최적화
    public BookAndHistoryDetail searchBookHistory(Long id, Pageable pageable) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_BOOK));
        Page<BookHistory> bookHistories = bookHistoryRepository.findAllByBook(book, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));
        return BookAndHistoryDetail.from(bookHistories);
    }
}
