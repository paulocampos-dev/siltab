package com.BYD.exception;

import com.BYD.enums.ErrorEnum;

public interface CustomExceptionInterface {
    ErrorEnum getCustomError();
    Exception getError();
}