package com.example.inandout.api.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JoinResponseDto {
    private static final String result = "인증 메일을 전송했습니다.";
    private Integer memberImageId;
}
