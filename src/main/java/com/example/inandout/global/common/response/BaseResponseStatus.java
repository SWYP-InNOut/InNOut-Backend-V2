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
    ACTIVE_MEMBER(5001, HttpStatus.BAD_REQUEST.value(), "이미 가입된 사용자입니다."),
    DUPLICATED_NICKNAME(5002, HttpStatus.BAD_REQUEST.value(), "중복된 닉네임입니다."),
    DUPLICATED_EMAIL(5003, HttpStatus.BAD_REQUEST.value(), "중복된 이메일입니다."),
    MEMBER_NOT_FOUND(5004, HttpStatus.BAD_REQUEST.value(), "회원이 존재하지 않습니다."),

    /**
     * 6000: 인증 오류
     */
    FAILED_SEND_EMAIL(4001, HttpStatus.BAD_REQUEST.value(), "인증 메일 전송에 실패하였습니다."),
    EXPIRED_REFRESHTOKEN(4002, HttpStatus.UNAUTHORIZED.value(), "RefreshToken 유효 기간이 만료되었습니다."),
    NOT_FOUND_REFRESHTOKEN(4003, HttpStatus.UNAUTHORIZED.value(), "존재하지 않는 RefreshToken입니다.");

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
