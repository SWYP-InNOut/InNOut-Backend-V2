package com.example.inandout.api.presentation.member;

import com.example.inandout.api.application.member.JoinService;
import com.example.inandout.api.application.member.LoginService;
import com.example.inandout.api.application.member.MemberSaveService;
import com.example.inandout.api.dto.auth.request.FindPasswordDto;
import com.example.inandout.api.dto.auth.request.JoinRequestDto;
import com.example.inandout.api.dto.auth.response.JoinResponseDto;
import com.example.inandout.api.dto.member.MemberNameDto;
import com.example.inandout.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberSaveService memberSaveService;
    private final JoinService joinService;
    private final LoginService loginService;

    @PostMapping("/checkSave")
    public void saveMember(@RequestBody @Validated MemberNameDto memberIdAndNameDto) {
        log.info(memberIdAndNameDto.getName());
        memberSaveService.saveMember(memberIdAndNameDto);
    }

    @PostMapping("/join")
    public BaseResponse<JoinResponseDto> join(@RequestBody @Validated JoinRequestDto joinRequestDto) {
        log.info("MemberController.join");
        int memberImageId = joinService.join(joinRequestDto);
        JoinResponseDto joinResponseDto = new JoinResponseDto("인증 메일을 전송했습니다.", memberImageId);
        return new BaseResponse<>(joinResponseDto);
    }

    @GetMapping("/auth/verify")
    public Object verifyEmail(@RequestParam("token") String token) {
        boolean isComplete = joinService.updateByVerifyToken(token);

        if (isComplete) {
            return new RedirectView("http://stuffinout.site/login");
        } else {
            return new RedirectView("http://stuffinout.site/error");    // 링크 만료 페이지로 이동
        }
    }

    @PostMapping("/find-password")
    public BaseResponse<String> findPassword(@RequestBody @Validated FindPasswordDto findPasswordDto) {
        loginService.findPassword(findPasswordDto.getEmail());
        return new BaseResponse<>("비밀번호 찾기가 완료되었습니다.");
    }
}
