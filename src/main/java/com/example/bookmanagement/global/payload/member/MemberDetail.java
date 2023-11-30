package com.example.bookmanagement.global.payload.member;

import com.example.bookmanagement.entity.Member;

import java.util.UUID;

public record MemberDetail(
        UUID id,
        String name,
        String email

) {

    public static MemberDetail from(Member m) {
        return new MemberDetail(m.getId(), m.getName(), m.getEmail());
    }
}
