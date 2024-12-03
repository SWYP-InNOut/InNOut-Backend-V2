package com.example.inandout.global.common.error.handler;

import com.example.inandout.global.common.error.exception.BaseException;
import com.example.inandout.global.common.error.response.BaseErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class BaseExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({BaseException.class})
    public BaseErrorResponse handle_BaseException(BaseException e) {
        log.error("[handle_BadRequest]", e);
        return new BaseErrorResponse(e.getExceptionStatus());
    }

}
