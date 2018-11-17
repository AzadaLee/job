package com.azada.job.event;

import com.azada.job.bean.ScheduleBean;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

/**
 * 结点内容清空事件
 * @author azada
 */
@Data
public class ScheduleImpNodeEmptyContentEvent extends ApplicationEvent {

    private ScheduleBean scheduleBean;

    public ScheduleImpNodeEmptyContentEvent(ScheduleBean scheduleBean) {
        super(scheduleBean);
        this.scheduleBean = scheduleBean;
    }
}
