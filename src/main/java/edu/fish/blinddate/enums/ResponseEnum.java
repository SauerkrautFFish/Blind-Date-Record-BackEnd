package edu.fish.blinddate.enums;

import lombok.Getter;

@Getter
public enum ResponseEnum {

    // 0 正常
    SUCCESS(0, "成功"),

    //-1 服务器错误
    SYSTEM_ERROR(-1, "系统异常!"),

    //-2x 参数校验
    EMAIL_NULL_INCORRECT(-21, "邮箱地址不能为空!"),
    EMAIL_FORMAT_INCORRECT(-22, "邮箱地址格式不正确!"),
    MISSING_PARAMS(-23, "参数缺失, 请输入完整!"),
    TOKEN_EXPIRE(-24, "状态已失效, 请重新登录!"),

    //-3x 业务错误
    ACCOUNT_EXISTS(-31, "该账号已存在!"),
    ACCOUNT_NOT_EXISTS_OR_PASSWORD_ERR(32, "账号不存在或密码错误!"),


    ;

    private final int code;

    private final String message;

    ResponseEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ResponseEnum getResEnum(int code) {
        ResponseEnum[] values = values();
        for(int i = 0; i < values.length; i++) {
            if(values[i].getCode() == code) {
                return values[i];
            }
        }

        return null;
    }
}
