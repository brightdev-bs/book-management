package com.example.bookmanagement.service;

import com.example.bookmanagement.entity.Member;
import com.example.bookmanagement.global.payload.member.SignupForm;
import com.example.bookmanagement.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    MemberService memberService;


    @DisplayName("회원가입")
    @Test
    void signup() {
        SignupForm form = getSimpleSignupForm();

        memberService.signup(form);

        then(memberRepository).should().save(any(Member.class));
    }

    @DisplayName("이미 사용중인 이메일이면 예외를 던진다.")
    @Test
    void signupFailWithDuplicatedEmail() {
        Member member = mock(Member.class);
        given(memberRepository.findByEmail(any(String.class))).willReturn(Optional.of(member));

        Assertions.assertThrows(DuplicateKeyException.class, () -> memberService.signup(getSimpleSignupForm()));
    }

    private SignupForm getSimpleSignupForm() {
        return new SignupForm("test", "test@gmail.com");
    }
}