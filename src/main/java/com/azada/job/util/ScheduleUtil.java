package com.azada.job.util;

import com.azada.job.bean.ScheduleBean;
import com.azada.job.constant.DistributeScheduleConstant;

import java.util.ArrayList;
import java.util.List;

public class ScheduleUtil {


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
    public static String generateServiceImplNodePathName (ScheduleBean scheduleBean) {
        String scheduleServicePath = generateServiceNodePathName(scheduleBean);
        String ip = IpUtil.LOCAL_IP;
        return scheduleServicePath.concat(DistributeScheduleConstant.DIRECTORY_CHARACTER).concat(ip);
    }

    /**
     * 分片
     * @param size 集合大小
     * @param counts 分组个数
     * @return
     */
    public static List<Integer> average(Integer size, Integer counts) {
        if (null == size || size.equals(0)
                || null == counts || counts.equals(0)) {
            return null;
        }
        List<Integer> result = new ArrayList<>(counts);
        Integer m = size%counts;
        Integer d = size/counts;

        if (!m.equals(0)) {
            for (int i = 0 ; i < counts; i++) {
                if (i < m) {
                    result.add(d + 1);
                } else {
                    result.add(d);
                }
            }
        } else {
            for (int i = 0 ; i < counts; i++) {
                result.add(d);
            }
        }
        return result;
    }
}
