package com.example.bookmanagement.global.payload.response;

import lombok.Getter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

@Getter
public class ApiResponse {

    private final String statusCode;
    private final Object data;

    private ApiResponse(String statusCode, Object data) {
        this.statusCode = statusCode;
        this.data = data;
    }

    public static ApiResponse of(String statusCode, Object data) {
        return new ApiResponse(statusCode, data);
    }

    public static ApiResponse of(String statusCode, BindingResult bindingResult) {
        return new ApiResponse(statusCode, createErrorMessage(bindingResult));
    }

    private static String createErrorMessage(BindingResult bindingResult) {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;

        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            if(!isFirst) {
                sb.append(", ");
            } else {
                isFirst = false;
            }
            sb.append("[");
            sb.append(fieldError.getField());
            sb.append("] ");
            sb.append(fieldError.getDefaultMessage());
        }

        return sb.toString();
    }
}
