package com.azada.job.framework;

import com.azada.job.annotation.DistributeSchedule;
import com.azada.job.bean.ScheduleBean;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContextException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author taoxiuma
 */
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
            String serviceModuleName = annotation.value();
            String classFullName = bean.getClass().getTypeName();
            Class clazz = bean.getClass();
            ScheduleBean scheduleBean = new ScheduleBean(serviceModuleName, classFullName, clazz, null);
            try {
                curatorClient.createScheduleServiceNode(scheduleBean);
            } catch (Exception e) {
                if (!(e instanceof KeeperException.NodeExistsException)) {
                    throw new ApplicationContextException(e.getLocalizedMessage(), e);
                }
            }
        }
        return bean;
    }
}
