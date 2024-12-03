package com.example.inandout.api.application.member;

import com.example.inandout.api.domain.member.entity.Member;
import com.example.inandout.api.domain.member.repository.MemberRepository;
import com.example.inandout.api.dto.auth.request.JoinRequestDto;
import com.example.inandout.api.dto.member.MemberNameDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberSaveService {
    private final MemberRepository memberRepository;

    public void saveMember(MemberNameDto memberNameDto) {
        Member newMember =
                Member.createGeneralMember("소민", "dd@dd", "password", 1, "authToken");
        memberRepository.save(newMember);
    }
}
