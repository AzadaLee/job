package com.azada.job.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 * 定时任务实例结点增加内容发布器
 *
 * @author taoxiuma
 */
@Component
public class ScheduleImpNodeAddContentPublisher implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publish(ScheduleImpNodeAddContentEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
