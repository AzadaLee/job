package com.ppdai.tars.job.config;

import com.ppdai.tars.job.constant.ResponseEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 封装异常
 *
 * @author taoxiuma
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TarsException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -973003277500078288L;

    private String code;

    public TarsException(String code, String message, String param) {
        super(message != null ? String.format(message, param) : message);
        this.code = code;
    }

    public TarsException(String code, String message) {
        this(code, message, null);
    }

    public TarsException(String msg) {
        this(ResponseEnum.SERVICE_EXCEPTION.getCode(), msg, null);
    }

    public TarsException(ResponseEnum response) {
        this(response.getCode(), response.getMessage(), null);
    }
}
