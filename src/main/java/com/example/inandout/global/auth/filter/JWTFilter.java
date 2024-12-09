package com.example.inandout.global.auth.filter;

import com.example.inandout.api.domain.member.entity.Member;
import com.example.inandout.api.domain.member.repository.MemberRepository;
import com.example.inandout.api.domain.member.value.MemberStatus;
import com.example.inandout.global.auth.domain.PrincipalDetails;
import com.example.inandout.api.application.auth.JWTProviderService;
import com.example.inandout.global.common.error.exception.MemberException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.example.inandout.global.common.response.BaseResponseStatus.MEMBER_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final MemberRepository memberRepository;
    private final JWTProviderService jwtProviderService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("CHECK JWT: JWTFilter.doFilterInternal");

        //request에서 Authorization 헤더를 찾음
        String authorization= request.getHeader("Authorization");

        //Authorization 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            System.out.println("token null");
            filterChain.doFilter(request, response);

            //조건이 해당되면 메소드 종료 (필수)
            return;
        }

        //Bearer 부분 제거 후 순수 토큰만 획득
        String token = authorization.split(" ")[1];

        //토큰 소멸 시간 검증
        if (jwtProviderService.isExpired(token)) {
            System.out.println("token expired");
            filterChain.doFilter(request, response);

            //조건이 해당되면 메소드 종료 (필수)
            return;
        }

        //토큰에서 memberId 획득
        Long memberId = jwtProviderService.getMemberId(token);

        if (memberId != null) {
            log.info(memberId.toString());
            Member member = memberRepository.findByStatusAndId(MemberStatus.ACTIVE, memberId)
                            .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
            saveAuthentication(member);

            filterChain.doFilter(request, response);
        }
    }

    private void saveAuthentication(Member member) {
        // UserDetails에 회원 정보 객체 담기
        PrincipalDetails principalDetails = new PrincipalDetails(member);

        // Authentication 객체 생성
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

        // SecurityContextHolder에 인증 객체 등록
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
