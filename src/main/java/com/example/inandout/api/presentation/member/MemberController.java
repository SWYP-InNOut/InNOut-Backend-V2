package com.example.inandout.api.presentation.member;

import com.example.inandout.api.application.member.MemberSaveService;
import com.example.inandout.api.dto.member.MemberNameDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberSaveService memberSaveService;

    @PostMapping("/checkSave")
    public void saveMember(@RequestBody MemberNameDto memberIdAndNameDto) {
        log.info(memberIdAndNameDto.getName());
        memberSaveService.saveMember(memberIdAndNameDto);
    }
}
