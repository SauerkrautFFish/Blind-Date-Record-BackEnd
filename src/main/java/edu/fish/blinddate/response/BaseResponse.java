package edu.fish.blinddate.response;

import edu.fish.blinddate.enums.ResponseEnum;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class BaseResponse<T> implements Serializable {

    private final Integer code;
    private final String message;
    private final T data;

    private BaseResponse() {
        throw new UnsupportedOperationException();
    }

    private BaseResponse(Integer code) {
        this(code, null, null);
    }

    private BaseResponse(Integer code, String message) {
        this(code, message, null);
    }

    private BaseResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    private BaseResponse(Integer code, T data) {
        this(code, null, data);
    }

    private BaseResponse(ResponseEnum responseEnum, T data) {
        this(responseEnum.getCode(), responseEnum.getMessage(), data);
    }

    public static <T> BaseResponse<T> success() {
        return new BaseResponse<>(ResponseEnum.SUCCESS.getCode());
    }

    // 自定义成功消息
    public static <T> BaseResponse<T> successMsg(String message) {
        return new BaseResponse<T>(ResponseEnum.SUCCESS.getCode(), message);
    }

    public static <T> BaseResponse<T> successData(T data) {
        return new BaseResponse<T>(ResponseEnum.SUCCESS.getCode(), data);
    }

    public static <T> BaseResponse<T> successMsgAndData(String message, T data) {
        return new BaseResponse<T>(ResponseEnum.SUCCESS.getCode(), message, data);
    }

    public static <T> BaseResponse<T> internalError() {
        return new BaseResponse<T>(ResponseEnum.SYSTEM_ERROR, null);
    }

    public static <T> BaseResponse<T> set(ResponseEnum responseEnum) {
        return new BaseResponse<T>(responseEnum, null);
    }

}
