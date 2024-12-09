package com.example.inandout.api.application.auth;

import com.example.inandout.api.infrastructure.redis.ValueRedisRepository;
import com.example.inandout.global.auth.domain.TokenInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTProviderService {
    @Autowired
    private ValueRedisRepository redisRepository;
    private SecretKey secretKey;
    private final Long ACCESSTOKEN_VALIDTIME = (60 * 1000L) * 30; // 30분
    private final Long REFRESHTOKEN_VALIDTIME = (60 * 1000L) * 60 * 24 * 7; // 7일

    public JWTProviderService(@Value("${jwt.secret}") String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public TokenInfo generateToken(Long memberId) {
        String accessToken = createAccessToken(memberId, ACCESSTOKEN_VALIDTIME);
        String refreshToken = createRefreshToken(REFRESHTOKEN_VALIDTIME);

        redisRepository.saveValueWithExpiry(refreshToken, String.valueOf(memberId), REFRESHTOKEN_VALIDTIME);

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

    public Boolean isExpired(String token) throws ExpiredJwtException{
        System.out.println("isExpired: "+token);
        System.out.println("secretKey: "+secretKey);
        System.out.println(Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date(System.currentTimeMillis())));
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date(System.currentTimeMillis()));
    }

    public Long getMemberId(String token) {
        String memberId = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

        return Long.parseLong(memberId);
    }
}
