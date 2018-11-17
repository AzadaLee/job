package com.azada.job.event;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

/**
 * 结点内容清空事件
 * @author azada
 */
@Data
public class ScheduleImpNodeEmptyContentEvent extends ApplicationEvent {

    private ScheduleImpNodeData scheduleImpNodeData;

    public ScheduleImpNodeEmptyContentEvent(ScheduleImpNodeData scheduleImpNodeData) {
        super(scheduleImpNodeData);
        this.scheduleImpNodeData = scheduleImpNodeData;
    }
}
