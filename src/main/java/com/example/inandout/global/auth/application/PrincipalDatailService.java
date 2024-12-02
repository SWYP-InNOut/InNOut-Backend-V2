package com.example.inandout.global.auth.application;

import com.example.inandout.api.domain.member.entity.Member;
import com.example.inandout.api.domain.member.repository.MemberRepository;
import com.example.inandout.global.auth.domain.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrincipalDatailService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("PrincipalDatailService");
        // TODO: 예외처리
        Member member = memberRepository.findByEmail(email).orElseThrow();
        return new PrincipalDetails(member);
    }
}
