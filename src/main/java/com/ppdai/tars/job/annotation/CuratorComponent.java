package com.ppdai.tars.job.annotation;


import com.ppdai.tars.job.constant.CuratorConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CuratorComponent {

    /**
     * 业务具体操作节点名，如果为空，则为spring bean name
     * @return
     */
    String value() default "";

    /**
     * 业务模块名
     * @return
     */
    String serviceModule() default CuratorConstant.NODE_SCHEDULE;
}
