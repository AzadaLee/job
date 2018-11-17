package com.azada.job.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 定时任务实例事件数据对象
 *
 * @author taoxiuma
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleImpNodeData {

    /**
     * 类名
     */
    private Class clazz;

    /**
     * 类全名
     */
    private String classFullName;

    /**
     * 节点数据
     */
    private String nodeData;
}
