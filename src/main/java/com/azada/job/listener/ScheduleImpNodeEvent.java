package com.azada.job.listener;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

/**
 * 定时任务实例事件
 *
 * @author taoxiuma
 */
@Data
public class ScheduleImpNodeEvent extends ApplicationEvent {


    private ScheduleImpNodeData scheduleImpNodeData;
    /**
     * Create a new ApplicationEvent.
     *
     * @param scheduleImpNodeData the object on which the event initially occurred (never {@code null})
     */
    public ScheduleImpNodeEvent(ScheduleImpNodeData scheduleImpNodeData) {
        super(scheduleImpNodeData);
        this.scheduleImpNodeData = scheduleImpNodeData;
    }
}
