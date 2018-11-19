package com.azada.job.constant;

import lombok.Getter;

/**
 * 返回信息枚举
 *
 * @author taoxiuma
 */
@Getter
public enum ResponseEnum {

    /**
     * 请求成功
     */
    SUCCESS("000000", "请求成功！"),
    /**
     * 发生未知异常
     */
    SERVER_ERROR("999999", "服务器繁忙，请稍后再试"),
    /**
     * 网络异常
     */
    CONNECTION_ERROR("888888", "网络连接异常"),
    /**
     * 业务校验异常
     */
    SERVICE_EXCEPTION("222222", "业务异常"),
    /**
     * 必输参数校验异常
     */
    PARAM_NULL("111111", "请求参数错误");

    /**
     * 返回值头部编码
     */
    private String code;

    /**
     * 返回值头部信息
     */
    private String message;

    private ResponseEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
