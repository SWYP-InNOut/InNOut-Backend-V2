package com.example.inandout.api.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {
    private Long memberId;
    private String nickname;
    private Integer memberImageId;
}
