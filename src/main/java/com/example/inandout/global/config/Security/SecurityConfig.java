package com.example.inandout.global.config.Security;

import com.example.inandout.api.domain.member.repository.MemberRepository;
import com.example.inandout.global.auth.filter.JWTFilter;
import com.example.inandout.global.auth.filter.LoginFilter;
import com.example.inandout.api.application.auth.JWTProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final MemberRepository memberRepository;
    private final JWTProviderService jwtProviderService;
    private final AuthenticationConfiguration authenticationConfiguration;

    // AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // csrf disable
        http.csrf(AbstractHttpConfigurer::disable);

        // Form 로그인 방식 disable
        http.formLogin(AbstractHttpConfigurer::disable);

        // http basic 인증 방식 disable
        http.httpBasic(AbstractHttpConfigurer::disable);

        // 세션 설정
        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));  // 세션을 STATELESS 상태로 설정

        // 필터 등록
        http.addFilterAt(new LoginFilter(memberRepository, jwtProviderService, authenticationManager(authenticationConfiguration)),
                UsernamePasswordAuthenticationFilter.class);

        http.addFilterBefore(new JWTFilter(memberRepository, jwtProviderService), LoginFilter.class);

        // 경로별 인가 작업
        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/login").permitAll() // 모든 권한 허용
                .requestMatchers("/").permitAll()
                .requestMatchers("/join").permitAll()
                .requestMatchers("/auth/verify").permitAll()
                .requestMatchers("/find-password").permitAll()
                .anyRequest().authenticated()); // 로그인 한 사용자만 접근 가능

        return http.build();
    }
}
