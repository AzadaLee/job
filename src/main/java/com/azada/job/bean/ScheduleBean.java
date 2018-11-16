package com.azada.job.bean;

import com.azada.job.constant.DistributeScheduleConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author taoxiuma
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleBean {

    /**
     * 业务模块名
     */
    private String scheduleServiceName = DistributeScheduleConstant.NODE_SCHEDULE;

    /**
     * 类全路劲
     */
    private String classFullName;

    /**
     * 类名
     */
    private Class clazz;

}
