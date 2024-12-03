package com.example.inandout.global.common.error.handler;

import com.example.inandout.global.common.error.exception.MemberException;
import com.example.inandout.global.common.error.response.BaseErrorResponse;
import jakarta.annotation.Priority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Priority(0)
@RestControllerAdvice
public class MemberExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MemberException.class)
    public BaseErrorResponse handel_MemberException(MemberException e) {
        log.error("[handel_MemberException]", e);
        return new BaseErrorResponse(e.getExceptionStatus());
    }

}
