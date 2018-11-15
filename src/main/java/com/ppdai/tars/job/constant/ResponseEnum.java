package com.ppdai.tars.job.constant;

import lombok.Getter;

/**
 * 返回信息枚举
 *
 * @author taoxiuma
 */
@Getter
public enum ResponseEnum {

    SUCCESS("000000", "请求成功！"),
    SERVER_ERROR("999999", "服务器繁忙，请稍后再试"),
    CONNECTION_ERROR("888888", "网络连接异常"),
    SERVICE_EXCEPTION("222222", "业务异常"),
    ACTIVITY_CONFLICT_EXCEPTION("200001", "活动策略冲突"),
    PARAM_NULL("111111", "请求参数错误"),
    TOKEN_ERROR("333333", "TOKEN信息错误"),
    IDENTITY_ERROR("555555", "身份信息待完善");
    private String code;

    private String message;

    private ResponseEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
