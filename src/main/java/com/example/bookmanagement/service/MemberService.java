package com.example.bookmanagement.service;

import com.example.bookmanagement.entity.Member;
import com.example.bookmanagement.global.constants.ErrorCode;
import com.example.bookmanagement.global.payload.member.MemberDetail;
import com.example.bookmanagement.global.payload.member.SignupForm;
import com.example.bookmanagement.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberDetail signup(SignupForm form) {
        Optional<Member> byEmail = memberRepository.findByEmail(form.email());

        if (byEmail.isPresent())
            throw new DuplicateKeyException(ErrorCode.DUPLICATED_EMAIL.getMessage());

        Member member = Member.of(form.name(), form.email());
        memberRepository.save(member);
        return MemberDetail.from(member);
    }
}
