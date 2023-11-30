package com.example.bookmanagement.global.exception;

import com.example.bookmanagement.global.constants.ErrorCode;
import lombok.Getter;

@Getter
public class BookNotAvailableException extends RuntimeException {

    private final ErrorCode errorCode;

    public BookNotAvailableException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
