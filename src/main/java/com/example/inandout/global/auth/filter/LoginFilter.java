package com.example.inandout.global.auth.filter;

import com.example.inandout.api.dto.auth.request.LoginRequestDto;
import com.example.inandout.global.auth.domain.PrincipalDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
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
        log.info("로그인 성공: LoginFilter.successfulAuthentication");

        // 로그인 성공
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        log.info("=============================================");
        log.info("email: " + principalDetails.getUsername());
        log.info("password: " + principalDetails.getPassword());
        log.info("=============================================");

        super.successfulAuthentication(request, response, chain, authentication);
    }
}
