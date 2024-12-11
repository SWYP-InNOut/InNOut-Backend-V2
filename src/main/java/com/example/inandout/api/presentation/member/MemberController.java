package com.example.inandout.api.presentation.member;

import com.example.inandout.api.application.auth.AuthService;
import com.example.inandout.api.application.member.JoinService;
import com.example.inandout.api.application.member.MemberService;
import com.example.inandout.api.application.member.MemberSaveService;
import com.example.inandout.api.dto.auth.request.FindPasswordDto;
import com.example.inandout.api.dto.auth.request.JoinRequestDto;
import com.example.inandout.api.dto.auth.response.JoinResponseDto;
import com.example.inandout.api.dto.member.MemberNameDto;
import com.example.inandout.global.auth.domain.TokenInfo;
import com.example.inandout.global.common.response.BaseResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String BLANK = " ";
    private static final String COOKIE_REFRESHTOKEN = "refreshToken=";
    private static final String COOKIE_FLAGS = "; Path=/; HttpOnly; Secure; ";
    private static final String COOKIE_MAXAGE = "Max-Age=";
    private static final String COOKIE_SAMESITE = "; SameSite=None";
    private static final String LOGIN_URL = "http://stuffinout.site/login";
    private static final String ERROR_URL = "http://stuffinout.site/error";
    private static final Long refreshTokenValidTime = (60 * 1000L) * 60 * 24 * 7;

    private final MemberSaveService memberSaveService;
    private final JoinService joinService;
    private final MemberService memberService;
    private final AuthService authService;

    @PostMapping("/checkSave")
    public void saveMember(@RequestBody @Validated MemberNameDto memberIdAndNameDto) {
        memberSaveService.saveMember(memberIdAndNameDto);
    }

    @PostMapping("/join")
    public BaseResponse<JoinResponseDto> join(@RequestBody @Validated JoinRequestDto joinRequestDto) {
        int memberImageId = joinService.join(joinRequestDto);
        JoinResponseDto joinResponseDto = new JoinResponseDto(memberImageId);
        return new BaseResponse<>(joinResponseDto);
    }

    @GetMapping("/auth/verify")
    public Object verifyEmail(@RequestParam("token") String token) {
        boolean isComplete = joinService.updateByVerifyToken(token);

        if (isComplete) {
            return new RedirectView(LOGIN_URL);
        } else {
            return new RedirectView(ERROR_URL);    // 링크 만료 페이지로 이동
        }
    }

    @PostMapping("/find-password")
    public BaseResponse<String> findPassword(@RequestBody @Validated FindPasswordDto findPasswordDto) {
        memberService.findPassword(findPasswordDto.getEmail());
        return new BaseResponse<>("비밀번호 찾기가 완료되었습니다.");
    }

    @GetMapping("/regenerate-token")
    public BaseResponse<String> reissue(HttpServletRequest request, HttpServletResponse response) {
        TokenInfo tokenInfo = authService.reissue(request);
        response.addHeader(HEADER_AUTHORIZATION, tokenInfo.getGrantType() + BLANK + tokenInfo.getAccessToken());
        response.setHeader(HttpHeaders.SET_COOKIE,
                COOKIE_REFRESHTOKEN
                        + tokenInfo.getRefreshToken()
                        + COOKIE_FLAGS
                        + COOKIE_MAXAGE
                        + refreshTokenValidTime
                        + COOKIE_SAMESITE);

        return new BaseResponse<>("토큰 발급이 완료되었습니다.");
    }
}
