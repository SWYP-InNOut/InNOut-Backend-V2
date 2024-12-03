package com.example.inandout.api.presentation.member;

import com.example.inandout.api.application.member.JoinService;
import com.example.inandout.api.application.member.MemberSaveService;
import com.example.inandout.api.dto.auth.request.JoinRequestDto;
import com.example.inandout.api.dto.auth.response.JoinResponseDto;
import com.example.inandout.api.dto.member.MemberNameDto;
import com.example.inandout.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberSaveService memberSaveService;
    private final JoinService joinService;

    @PostMapping("/checkSave")
    public void saveMember(@RequestBody MemberNameDto memberIdAndNameDto) {
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
}
