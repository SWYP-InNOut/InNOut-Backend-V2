package com.example.inandout.api.application.member;

import com.example.inandout.api.domain.member.entity.Member;
import com.example.inandout.api.domain.member.repository.MemberRepository;
import com.example.inandout.api.domain.member.value.LoginType;
import com.example.inandout.global.auth.util.EmailUtil;
import com.example.inandout.global.common.error.exception.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Optional;

import static com.example.inandout.global.common.response.BaseResponseStatus.MEMBER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LoginService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailUtil emailUtil;

    public void findPassword(String email) {
        Member member = validateGeneralMember(email);

        // 임시 비밀번호 만들기
        String newPwd = RandomStringUtils.randomAlphanumeric(10);
        member.updatePassword(passwordEncoder.encode(newPwd));
        emailUtil.sendPasswordEmail(email, newPwd);
    }

    private Member validateGeneralMember(String email) {
         return memberRepository.findByLoginTypeAndEmail(LoginType.GENERAL, email)
                 .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
    }
}
