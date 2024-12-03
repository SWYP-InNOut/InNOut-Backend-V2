package com.example.inandout.global.auth.util;

import com.example.inandout.global.auth.domain.TokenInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.sql.Date;

@Component
public class JWTUtil {
    private SecretKey secretKey;
    private final Long ACCESSTOKEN_VALIDTIME = (60 * 1000L) * 30; // 30분
    private final Long REFRESHTOKEN_VALIDTIME = (60 * 1000L) * 60 * 24 * 7; // 7일

    public JWTUtil(@Value("${jwt.secret}") String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public TokenInfo generateToken(Long memberId) {
        String accessToken = createAccessToken(memberId, ACCESSTOKEN_VALIDTIME);
        String refreshToken = createRefreshToken(REFRESHTOKEN_VALIDTIME);

        return TokenInfo.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private String createAccessToken(Long memberId, Long expiredMs) {
        Claims claims = Jwts.claims().subject(String.valueOf(memberId)).build();
        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    private String createRefreshToken(Long expiredMs) {
        return Jwts.builder()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }
}
