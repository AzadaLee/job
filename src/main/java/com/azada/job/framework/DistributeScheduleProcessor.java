package com.azada.job.framework;

import com.azada.job.annotation.DistributeSchedule;
import com.azada.job.bean.ScheduleBean;
import com.azada.job.constant.DistributeScheduleConstant;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContextException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;

@Component
public class DistributeScheduleProcessor implements BeanPostProcessor {

    @Resource
    private CuratorClient curatorClient;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        DistributeSchedule annotation = bean.getClass().getAnnotation(DistributeSchedule.class);
        if (null != annotation) {
            //增加开关
            try {
                Field switcher = bean.getClass().getField(DistributeScheduleConstant.SCHEDULE_SWITCHER_FIELD);
                switcher.setAccessible(true);
                Object switcherValue = switcher.get(bean);
                if (null != switcherValue && DistributeScheduleConstant.SCHEDULE_SWITCHER_DEFAULT_VALUE
                        .equalsIgnoreCase(((String)switcherValue).trim())) {
                    String serviceModuleName = annotation.value();
                    String classFullName = bean.getClass().getTypeName();
                    Class clazz = bean.getClass();
                    ScheduleBean scheduleBean = new ScheduleBean(serviceModuleName, classFullName, clazz, null);
                    try {
                        curatorClient.createScheduleServiceNode(scheduleBean);
                    } catch (Exception e) {
                        throw new ApplicationContextException(e.getLocalizedMessage(), e);
                    }
                }
            } catch (Exception e) {
                return bean;
            }
        }
        return bean;
    }
}
