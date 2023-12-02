package com.example.bookmanagement.fixture;

import com.example.bookmanagement.entity.Member;

public class MemberFixture {

    public static Member getDefaultMember() {
        return Member.of("tester", "tester@gmail.com");
    }
}
