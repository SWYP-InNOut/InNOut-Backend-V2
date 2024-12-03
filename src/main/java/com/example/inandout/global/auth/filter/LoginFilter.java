package com.example.inandout.global.auth.filter;

import com.example.inandout.api.domain.member.entity.Member;
import com.example.inandout.api.domain.member.repository.MemberRepository;
import com.example.inandout.api.dto.auth.request.LoginRequestDto;
import com.example.inandout.api.dto.auth.response.LoginResponseDto;
import com.example.inandout.global.auth.domain.PrincipalDetails;
import com.example.inandout.global.auth.domain.TokenInfo;
import com.example.inandout.global.auth.util.JWTUtil;
import com.example.inandout.global.common.response.BaseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final MemberRepository memberRepository;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final Long refreshTokenValidTime = (60 * 1000L) * 60 * 24 * 7; // 7일

    // "/login"으로 요청이 오면 실행되는 함수
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("로그인: LoginFilter.attemptAuthentication");

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // email, password 받기
            LoginRequestDto loginRequestDto = objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);

            // email, password 이용해서 token 발급
            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword());

            // 로그인 인증
            return authenticationManager.authenticate(authenticationToken);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        log.info("=============================================");
        log.info("로그인 성공: LoginFilter.successfulAuthentication");
        log.info("email: " + principalDetails.getUsername());
        log.info("password: " + principalDetails.getPassword());
        log.info("=============================================");

        Optional<Member> member = memberRepository.findByEmail(principalDetails.getUsername());

        if (member.isEmpty()) {
            response.setStatus(401);
            return;
        }

        responseToken(response, member.get());

        // TODO: redis에 refreshToken, memberId 저장

    }

    private void responseToken(HttpServletResponse response, Member member) throws IOException {
        TokenInfo tokenInfo = jwtUtil.generateToken(member.getId());

        // 응답의 콘텐츠 타입을 JSON으로 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        response.addHeader("Authorization", tokenInfo.getGrantType() + " " + tokenInfo.getAccessToken());
        response.setHeader(HttpHeaders.SET_COOKIE, "refreshToken=" + tokenInfo.getRefreshToken() + "; Path=/; HttpOnly; Secure; Max-Age=" + refreshTokenValidTime + "; SameSite=None");

        // JSON 응답 작성
        LoginResponseDto loginResponseDto = new LoginResponseDto(member.getId(), member.getName(), member.getMemberImageId());
        PrintWriter writer = response.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        writer.write(mapper.writeValueAsString(new BaseResponse<>(loginResponseDto)));
        writer.flush();
        writer.close();
    }


}
