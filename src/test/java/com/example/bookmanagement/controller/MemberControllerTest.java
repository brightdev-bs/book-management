package com.example.bookmanagement.controller;

import com.example.bookmanagement.entity.Member;
import com.example.bookmanagement.global.constants.ErrorCode;
import com.example.bookmanagement.global.payload.member.SignupForm;
import com.example.bookmanagement.repository.MemberRepository;
import com.example.bookmanagement.service.MemberService;
import com.fasterxml.jackson.core.JsonProcessingException;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class MemberControllerTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

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

    final SignupForm form = new SignupForm("test", "test@gmail.com");

    @DisplayName("회원가입")
    @Test
    void signup() throws Exception {
        mockMvc.perform(post("/members/sign-up")
                .content(objectMapper.writeValueAsString(form))
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(jsonPath("statusCode").value(HttpStatus.CREATED.toString()))
                .andExpect(jsonPath("$.data.id").hasJsonPath())
                .andExpect(jsonPath("$.data.name").hasJsonPath())
                .andExpect(jsonPath("$.data.email").hasJsonPath());
    }

    @DisplayName("회원가입 실패: 이메일 중복")
    @Test
    void signupFailWithDuplicatedEmail() throws Exception {
        memberRepository.save(Member.of("dummy", "test@gmail.com"));
        mockMvc.perform(post("/members/sign-up")
                        .content(objectMapper.writeValueAsString(form))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(jsonPath("statusCode").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("data").value(ErrorCode.DUPLICATED_EMAIL.getMessage()));
    }
}