package com.azada.job.util;

import com.azada.job.bean.ScheduleBean;
import com.azada.job.constant.DistributeScheduleConstant;

/**
 * @author taoxiuma
 */
public class NodePathUtil {

    /**
     * @param scheduleBean
     * @return
     */
    public static String generateServiceNodePathName (ScheduleBean scheduleBean) {
        String scheduleServicePath = DistributeScheduleConstant.DIRECTORY_CHARACTER.concat(scheduleBean.getScheduleServiceName())
                .concat(DistributeScheduleConstant.DIRECTORY_CHARACTER).concat(scheduleBean.getClassFullName());
        return scheduleServicePath;
    }

    /**
     * 生成serviceSchedule实例节点路劲名
     * @param scheduleBean
     * @return
     */
    public static String generateServiceImplNodePathName (ScheduleBean scheduleBean, Integer port) {
        String scheduleServicePath = generateServiceNodePathName(scheduleBean);
        String ip = IpUtil.getLocalIP();
        return scheduleServicePath.concat(DistributeScheduleConstant.DIRECTORY_CHARACTER).concat(ip).concat(":" + port);
    }
}
