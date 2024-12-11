package com.example.inandout.global.auth.filter;

import com.example.inandout.api.application.auth.JWTProviderService;
import com.example.inandout.api.domain.member.entity.Member;
import com.example.inandout.api.domain.member.repository.MemberRepository;
import com.example.inandout.api.domain.member.value.LoginType;
import com.example.inandout.api.dto.auth.request.LoginRequestDto;
import com.example.inandout.api.dto.auth.response.LoginResponseDto;
import com.example.inandout.global.auth.domain.PrincipalDetails;
import com.example.inandout.global.auth.domain.TokenInfo;
import com.example.inandout.global.common.error.exception.MemberException;
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

import static com.example.inandout.global.common.response.BaseResponseStatus.MEMBER_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private static final String CONTENTTYPE = "application/json";
    private static final String CHARACTOR_ENCODING = "utf-8";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String BLANK = " ";
    private static final String COOKIE_REFRESHTOKEN = "refreshToken=";
    private static final String COOKIE_FLAGS = "; Path=/; HttpOnly; Secure; ";
    private static final String COOKIE_MAXAGE = "Max-Age=";
    private static final String COOKIE_SAMESITE = "; SameSite=None";
    private static final Long refreshTokenValidTime = (60 * 1000L) * 60 * 24 * 7; // 7일

    private final MemberRepository memberRepository;
    private final JWTProviderService jwtProviderService;
    private final AuthenticationManager authenticationManager;

    // "/login"으로 요청이 오면 실행되는 함수
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
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

        Member member = memberRepository.findByLoginTypeAndEmail(LoginType.GENERAL, principalDetails.getUsername())
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        TokenInfo tokenInfo = jwtProviderService.generateToken(member.getId());

        setResponse(response, member, tokenInfo);
    }

    private void setResponse(HttpServletResponse response, Member member, TokenInfo tokenInfo) throws IOException {
        setTokenInResponseHeaders(response, tokenInfo);
        setResponseBody(response, member);
    }

    private void setTokenInResponseHeaders(HttpServletResponse response, TokenInfo tokenInfo) {
        // 응답의 콘텐츠 타입을 JSON으로 설정
        response.setContentType(CONTENTTYPE);
        response.setCharacterEncoding(CHARACTOR_ENCODING);
        response.addHeader(HEADER_AUTHORIZATION, tokenInfo.getGrantType() + BLANK + tokenInfo.getAccessToken());
        response.setHeader(HttpHeaders.SET_COOKIE,
                COOKIE_REFRESHTOKEN
                        + tokenInfo.getRefreshToken()
                        + COOKIE_FLAGS
                        + COOKIE_MAXAGE
                        + refreshTokenValidTime
                        + COOKIE_SAMESITE);
    }

    private void setResponseBody(HttpServletResponse response, Member member) throws IOException {
        // JSON 응답 작성
        LoginResponseDto loginResponseDto = new LoginResponseDto(member.getId(), member.getName(), member.getMemberImageId());
        PrintWriter writer = response.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        writer.write(mapper.writeValueAsString(new BaseResponse<>(loginResponseDto)));
        writer.flush();
        writer.close();
    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        //로그인 실패시 401 응답 코드 반환
        log.info("unsuccessfulAuthentication: " + failed.getMessage());
        response.setStatus(401);
    }
}
