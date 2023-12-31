package com.example.bookmanagement.controller;

import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.entity.BookHistory;
import com.example.bookmanagement.entity.Member;
import com.example.bookmanagement.fixture.BookFixture;
import com.example.bookmanagement.fixture.BookHistoryFixture;
import com.example.bookmanagement.fixture.MemberFixture;
import com.example.bookmanagement.global.constants.ErrorCode;
import com.example.bookmanagement.global.payload.book.BookBorrowForm;
import com.example.bookmanagement.global.payload.book.BookRegisterFrom;
import com.example.bookmanagement.global.payload.book.BookReturnForm;
import com.example.bookmanagement.global.payload.book.BookUpdateForm;
import com.example.bookmanagement.repository.BookCacheRepository;
import com.example.bookmanagement.repository.BookHistoryRepository;
import com.example.bookmanagement.repository.BookRepository;
import com.example.bookmanagement.repository.MemberRepository;
import com.example.bookmanagement.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class BookControllerTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    BookHistoryRepository bookHistoryRepository;
    @Autowired
    BookCacheRepository bookCacheRepository;

    @Autowired
    BookService bookService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @DisplayName("도서 대출")
    @Test
    void borrowBook() throws Exception {
        Member member = memberRepository.save(MemberFixture.getDefaultMember());
        Book book = bookRepository.save(BookFixture.getDefaultBook());

        BookBorrowForm form = new BookBorrowForm(member.getId(), book.getId());

        mockMvc.perform(post("/books/borrow")
                        .content(objectMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(jsonPath("statusCode").value(HttpStatus.OK.toString()))
                .andExpect(jsonPath("data.bookId").value(book.getId()))
                .andExpect(jsonPath("data.bookName").value(book.getName()))
                .andExpect(jsonPath("data.borrowedBy").value(member.getName()))
                .andExpect(jsonPath("data.borrowedAt").value(LocalDate.now().toString()));
    }

    @DisplayName("도서 대출 실패: 도서가 이용 불가능한 상태")
    @Test
    void borrowBookFailed() throws Exception {
        Member member = memberRepository.save(MemberFixture.getDefaultMember());
        Book book = bookRepository.save(BookFixture.getDefaultBook());
        book.setBorrowed(true);

        BookBorrowForm form = new BookBorrowForm(member.getId(), book.getId());

        mockMvc.perform(post("/books/borrow")
                        .content(objectMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(jsonPath("statusCode").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("data").value(ErrorCode.NOT_AVAILABLE_BOOK.getMessage()));
    }

    @DisplayName("도서 대출 실패: 연체자")
    @Test
    void borrowBookFailedWithDelayed() throws Exception {
        Member member = memberRepository.save(MemberFixture.getDefaultMember());
        Book book = bookRepository.save(BookFixture.getDefaultBook());
        bookCacheRepository.setDelayedMember(member.getId());

        BookBorrowForm form = new BookBorrowForm(member.getId(), book.getId());

        mockMvc.perform(post("/books/borrow")
                        .content(objectMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(jsonPath("statusCode").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("data").value(ErrorCode.DELAYED_USER.getMessage()));
    }

    @DisplayName("도서 반납")
    @Test
    void returnBook() throws Exception {
        Member member = memberRepository.save(MemberFixture.getDefaultMember());
        Book book = bookRepository.save(BookFixture.getDefaultBook());
        book.setBorrowed(true);
        BookHistory history = bookHistoryRepository.save(BookHistoryFixture.getDefaultHistory(book, member));

        BookReturnForm form = new BookReturnForm(book.getId());

        mockMvc.perform(post("/books/return")
                        .content(objectMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(jsonPath("statusCode").value(HttpStatus.OK.toString()))
                .andExpect(jsonPath("data.bookName").value(book.getName()))
                .andExpect(jsonPath("data.borrowedAt").value(history.getBorrowedAt().toString()))
                .andExpect(jsonPath("data.returnedAt").value(LocalDate.now().toString()));
    }

    @DisplayName("도서 등록")
    @Test
    void registerBook() throws Exception {
        BookRegisterFrom form = new BookRegisterFrom("test", "tester");
        mockMvc.perform(post("/books/register")
                        .content(objectMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(jsonPath("statusCode").value(HttpStatus.CREATED.toString()))
                .andExpect(jsonPath("data.bookName").value("test"))
                .andExpect(jsonPath("data.author").value("tester"))
                .andExpect(jsonPath("data.createdAt").value(LocalDate.now().toString()))
                .andExpect(jsonPath("data.updatedAt").value(LocalDate.now().toString()));


    }

    @DisplayName("도서 업데이트")
    @Test
    void updateBook() throws Exception {
        Book book = bookRepository.save(BookFixture.getDefaultBook());
        BookUpdateForm form = new BookUpdateForm(book.getId(), "changedBook", "changedAuthor");
        mockMvc.perform(patch("/books/update")
                        .content(objectMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(jsonPath("statusCode").value(HttpStatus.OK.toString()))
                .andExpect(jsonPath("data.bookName").value("changedBook"))
                .andExpect(jsonPath("data.author").value("changedAuthor"))
                .andExpect(jsonPath("data.createdAt").value(LocalDate.now().toString()))
                .andExpect(jsonPath("data.updatedAt").value(LocalDate.now().toString()));
    }

    @DisplayName("도서 업데이트 실패")
    @Test
    void updateBookFail() throws Exception {
        BookUpdateForm form = new BookUpdateForm(1L, "changedBook", "changedAuthor");
        mockMvc.perform(patch("/books/update")
                        .content(objectMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(jsonPath("statusCode").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("data").value(ErrorCode.NOT_FOUND_BOOK.getMessage()));
    }

    @DisplayName("도서 조회")
    @Test
    void searchHistory() throws Exception {
        Long id = setupBookAndHistory();
        System.out.println(id);
        mockMvc.perform(get("/books/" + id + "/history"))
                .andDo(print())
                .andExpect(jsonPath("statusCode").value(HttpStatus.OK.toString()))
                .andExpect(jsonPath("data.histories.size()").value(5));
    }

    private Long setupBookAndHistory() {
        Book book = BookFixture.getBookWithBookName("test1");
        bookRepository.save(book);
        bookHistoryRepository.save(BookHistoryFixture.generateCompleteHistory(book));
        bookHistoryRepository.save(BookHistoryFixture.generateCompleteHistory(book));
        bookHistoryRepository.save(BookHistoryFixture.generateCompleteHistory(book));
        bookHistoryRepository.save(BookHistoryFixture.generateCompleteHistory(book));
        bookHistoryRepository.save(BookHistoryFixture.generateCompleteHistory(book));

        return book.getId();

    }
}