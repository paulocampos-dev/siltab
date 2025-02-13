package com.BYD.exception;

import com.BYD.enums.ErrorEnum;

public class DBException extends RuntimeException implements CustomExceptionInterface {
    private final ErrorEnum errorEnum;
    private final Exception ex;

    public DBException(ErrorEnum errorEnum, Exception ex) {
        super();
        this.errorEnum = errorEnum;
        this.ex = ex;
    }

    @Override
    public ErrorEnum getCustomError() {
        return errorEnum;
    }

    @Override
    public Exception getError() {
        return ex;
    }
}