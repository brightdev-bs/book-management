package com.example.bookmanagement.global.exception;

import com.example.bookmanagement.global.constants.ErrorCode;

public class DelayedMemberException extends RuntimeException {

    private final ErrorCode errorCode;

    public DelayedMemberException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
