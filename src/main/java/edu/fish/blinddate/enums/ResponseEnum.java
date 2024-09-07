package edu.fish.blinddate.enums;

import lombok.Getter;

@Getter
public enum ResponseEnum {

    // 0 正常
    SUCCESS(0, "成功"),

    //-1 服务器错误
    SYSTEM_ERROR(-1, "系统异常!"),

    //-2x 业务错误
    EMAIL_NULL_INCORRECT(-21, "邮箱地址不能为空!"),
    MISSING_PARAMS(-22, "参数缺失, 请输入完整!"),
    TOKEN_EXPIRE(-23, "状态已失效, 请重新登录!"),

    ACCOUNT_EXISTS(-24, "该账号已存在!"),
    ACCOUNT_NOT_EXISTS_OR_PASSWORD_ERR(25, "账号不存在或密码错误!"),

    GPT_CALLED_ERROR(26, "GPT调用异常"),
    CANDIDATE_DONT_EXISTS(27, "候选人不存在"),

    RECORD_DATE_DUPLICATION(28, "记录日期重复"),

    GENERATING_REPORT(29, "报告正在生成中"),

    REPORT_NOT_EXISTS(30, "报告不存在"),
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
