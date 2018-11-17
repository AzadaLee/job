package com.azada.job.event;


import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 * 定时任务实例结点内容清空事件发布器
 * @author azada
 */
@Component
public class ScheduleImpNodeEmptyContentPublisher implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publish(ScheduleImpNodeEmptyContentEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
