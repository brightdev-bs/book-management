package com.example.bookmanagement.controller;

import com.example.bookmanagement.global.payload.member.MemberDetail;
import com.example.bookmanagement.global.payload.member.SignupForm;
import com.example.bookmanagement.global.payload.response.ApiResponse;
import com.example.bookmanagement.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/members")
@RestController
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/sign-up")
    public ApiResponse signup(@RequestBody SignupForm form) {
        MemberDetail response = memberService.signup(form);
        return ApiResponse.of(HttpStatus.CREATED.toString(), response);
    }

}
