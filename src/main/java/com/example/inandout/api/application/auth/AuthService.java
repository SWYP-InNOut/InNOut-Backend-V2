package com.example.inandout.api.application.auth;

import com.example.inandout.api.domain.member.repository.MemberRepository;
import com.example.inandout.api.domain.member.value.MemberStatus;
import com.example.inandout.api.infrastructure.redis.ValueRedisRepository;
import com.example.inandout.global.auth.domain.TokenInfo;
import com.example.inandout.global.common.error.exception.MemberException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.inandout.global.common.response.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JWTProviderService jwtProviderService;
    private final ValueRedisRepository redisRepository;
    private final MemberRepository memberRepository;

    public TokenInfo reissue(String refreshToken) {
        validateRefreshToken(refreshToken);
        Long memberId = getMemberIdFromRefreshToken(refreshToken);
        validateMember(memberId);

        return jwtProviderService.generateToken(memberId);
    }

    private void validateRefreshToken(String refreshToken) {
        try {
            jwtProviderService.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            redisRepository.deleteRefreshToken(refreshToken);
            throw new MemberException(EXPIRED_REFRESHTOKEN);
        }
    }

    private Long getMemberIdFromRefreshToken(String refreshToken) {
        Long memberId = redisRepository.getValue(refreshToken);

        if (memberId == null) {
            throw new MemberException(NOT_FOUND_REFRESHTOKEN);
        }
        redisRepository.deleteRefreshToken(refreshToken);

        return memberId;
    }

    private void validateMember(Long memberId) {
        memberRepository.findByStatusAndId(MemberStatus.ACTIVE, memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
    }
}
