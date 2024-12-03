package com.example.inandout.global.common.error.exception;

import com.example.inandout.global.common.response.ResponseStatus;

public class MemberException extends BaseException {
    public MemberException(ResponseStatus exceptionStatus) {
        super(exceptionStatus);
    }
}
