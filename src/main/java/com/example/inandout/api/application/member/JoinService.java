package com.example.inandout.api.application.member;

import com.example.inandout.api.domain.member.entity.Member;
import com.example.inandout.api.domain.member.repository.MemberRepository;
import com.example.inandout.api.domain.member.value.LoginType;
import com.example.inandout.api.domain.member.value.MemberStatus;
import com.example.inandout.api.dto.auth.request.JoinRequestDto;
import com.example.inandout.api.application.auth.EmailService;
import com.example.inandout.global.common.error.exception.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.example.inandout.global.common.response.BaseResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class JoinService {
    private final EmailService emailService;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public int join(JoinRequestDto joinRequestDto) {
        Optional<Member> member = memberRepository.findByLoginTypeAndEmail(LoginType.GENERAL, joinRequestDto.getEmail());

        int memberImageId = (int) ((Math.random()*6)+1);

        // 존재하는 회원이 없음
        if (member.isEmpty()) {
            validateDuplicateUsername(joinRequestDto.getUsername());
            Member savedMember = saveMember(joinRequestDto, memberImageId);

            emailService.sendEmail(savedMember);

            return memberImageId;
        }

        // 존재하는 회원이 있음
        validateGeneralActiveMember(member.get()); // ACTIVE, INACTIVE 회원인지 확인
        validateExpiredToken(member.get()); // 토큰이 만료되지 않았는지 확인

        updateTokenAndSendEmail(member.get());

        return memberImageId;
    }

    private void validateDuplicateUsername(String username) {
        boolean isExistName = memberRepository.existsByName(username);
        if (isExistName) {
            log.error(DUPLICATED_NICKNAME.getMessage());
            throw new MemberException(DUPLICATED_NICKNAME);
        }
    }

    private Member saveMember(JoinRequestDto joinRequestDto, int memberImageId) {
        String authToken = UUID.randomUUID().toString();
        Member member = Member.createGeneralMember(joinRequestDto.getUsername(),
                joinRequestDto.getEmail(),
                bCryptPasswordEncoder.encode(joinRequestDto.getPassword()),
                memberImageId,
                authToken);

        memberRepository.save(member);
        return member;
    }

    private void validateGeneralActiveMember(Member member) {
        if (!memberRepository.existsByStatusAndId(MemberStatus.NONCERTIFIED, member.getId())) {
            log.error(ACTIVE_MEMBER.getMessage());
            throw new MemberException(ACTIVE_MEMBER);
        }
    }

    private void validateExpiredToken(Member member) {
        if (!isExpired(member)) {
            log.error(DUPLICATED_EMAIL.getMessage()); // "이메일 인증을 진행해주세요."
            throw new MemberException(DUPLICATED_EMAIL);  // 존재하는 회원이 있음 + 토큰 만료 x
        }
    }

    private boolean isExpired(Member member) {
        LocalDateTime joinRequestTime = LocalDateTime.now();
        LocalDateTime generatedTime = member.getUpdatedAt();
//        LocalDateTime expirationDateTime = generatedTime.plusDays(1);
        LocalDateTime expirationDateTime = generatedTime.plusSeconds(60*10); // 유효기한 10분

        log.info("expiration date time: {}", expirationDateTime);
        log.info("requested time: {}", joinRequestTime);

        if (joinRequestTime.isAfter(expirationDateTime)) {
            return true;
        }

        return false;
    }

    private void updateTokenAndSendEmail(Member member) {
        String authToken = UUID.randomUUID().toString();
        member.updateToken(authToken); // 토큰 만료됨 -> 재발급하고 이메일 다시 보냄

        emailService.sendEmail(member);
    }

    public boolean updateByVerifyToken(String token) {
        Member member = memberRepository.findByAuthToken(token)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        if (!isExpired(member)) {
            member.updateStatus(MemberStatus.ACTIVE);
            return true;
        } else {
            return false;
        }
    }
}
