package edu.fish.blinddate.exception;

import edu.fish.blinddate.enums.ResponseEnum;

public class BaseException extends Exception {
    private ResponseEnum responseEnum;

    private BaseException() {
        throw new UnsupportedOperationException();
    }

    public BaseException(ResponseEnum responseEnum) {
        this.responseEnum = responseEnum;
    }

    public ResponseEnum getCodeAndMsg() {
        return this.responseEnum;
    }

    public int getCode() {
        return this.responseEnum.getCode();
    }

    public String getMessage() {
        return this.responseEnum.getMessage();
    }
}
