package com.example.bookmanagement.global.constants;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "이미 사용중인 이메일입니다."),
    NOT_FOUND_MEMBER(HttpStatus.BAD_REQUEST, "회원 정보가 없습니다."),
    NOT_FOUND_BOOK(HttpStatus.BAD_REQUEST, "책 정보가 없습니다."),
    NOT_AVAILABLE_BOOK(HttpStatus.BAD_REQUEST, "이미 누군가 책을 이용중입니다."),
    NOT_FOUND_BOOK_HISTORY(HttpStatus.BAD_REQUEST, "책을 빌린 기록이 없습니다."),
    ;

    private HttpStatus httpStatus;
    private String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
