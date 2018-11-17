package com.azada.job.event;

import com.azada.job.bean.ScheduleBean;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

/**
 * 定时任务实例事件
 *
 * @author taoxiuma
 */
@Data
public class ScheduleImpNodeAddContentEvent extends ApplicationEvent {


    private ScheduleBean scheduleBean;
    /**
     * Create a new ApplicationEvent.
     *
     * @param scheduleBean the object on which the event initially occurred (never {@code null})
     */
    public ScheduleImpNodeAddContentEvent(ScheduleBean scheduleBean) {
        super(scheduleBean);
        this.scheduleBean = scheduleBean;
    }
}
