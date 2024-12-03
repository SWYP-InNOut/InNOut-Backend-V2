package com.example.inandout.global.common.response;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum BaseResponseStatus implements ResponseStatus {
    /**
     * 1000: 요청 성공 (OK)
     */
    SUCCESS(1000, HttpStatus.OK.value(), "요청에 성공하였습니다."),

    /**
     * 5000: 회원 정보 오류
     */
    ACTIVE_MEMBER(5006, HttpStatus.BAD_REQUEST.value(), "이미 가입된 사용자입니다."),
    DUPLICATED_NICKNAME(5005, HttpStatus.BAD_REQUEST.value(), "중복된 닉네임입니다."),
    DUPLICATED_EMAIL(5004, HttpStatus.BAD_REQUEST.value(), "중복된 이메일입니다."),;

    private final int code;
    private final int status;
    private final String message;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
