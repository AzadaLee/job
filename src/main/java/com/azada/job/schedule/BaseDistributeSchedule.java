package com.azada.job.schedule;

import com.azada.job.annotation.DistributeSchedule;
import com.azada.job.bean.ScheduleBean;
import com.azada.job.config.TarsException;
import com.azada.job.constant.DistributeScheduleConstant;
import com.azada.job.framework.DistributeScheduleCuratorComponent;
import com.azada.job.util.CuratorFrameworkUtils;
import com.azada.job.util.IpUtil;
import com.azada.job.util.NodePathUtil;
import com.azada.job.util.ScheduleUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author azada
 */
@Component
@Slf4j
public abstract class BaseDistributeSchedule implements IDistributeSchedule{

    @Resource
    private CuratorFrameworkUtils curatorFrameworkUtils;

    @Resource
    private DistributeScheduleCuratorComponent distributeScheduleCuratorComponent;

    protected void init() {
        DistributeSchedule annotation = this.getClass().getAnnotation(DistributeSchedule.class);
        if (null == annotation) {
            throw new TarsException("distribute schedule must annotate by @DistributeSchedule");
        }
        String serviceModuleName = annotation.value();
        String classFullName = this.getClass().getTypeName();
        Class clazz = this.getClass();
        ScheduleBean scheduleBean = new ScheduleBean(serviceModuleName, classFullName, clazz, null);
        if (!distributeScheduleCuratorComponent.acquireLock(scheduleBean)) {
            //获取锁失败
            return;
        }

        String scheduleNodePathName = DistributeScheduleConstant.DIRECTORY_CHARACTER.concat(scheduleBean.getScheduleServiceName())
                .concat(DistributeScheduleConstant.DIRECTORY_CHARACTER).concat(classFullName);
        List<String> subNodes;
        try {
            subNodes = curatorFrameworkUtils.getChildrenNode(scheduleNodePathName);
        } catch (Exception e) {
            log.error("node {} get sub node error :{}", scheduleNodePathName, e);
            return;
        }
        List<Long> idList = this.getIdList();

        if (CollectionUtils.isEmpty(subNodes) || CollectionUtils.isEmpty(idList)) {
            log.info("schedule {} there is data to processed or there is no schedule impl. ", classFullName);
            try {
                distributeScheduleCuratorComponent.releaseLockGuaranteed(scheduleBean);
            } catch (Exception e) {
                log.info("schedule {} release lock error ：{} ", classFullName, e);
            }
            return;
        }
        List<Integer> idCountsList = ScheduleUtil.average(idList.size(), subNodes.size());
        writeDataToNode(subNodes, idList, idCountsList, scheduleBean);
    }

    /**
     * 往应用实例节点写数据
     * @param scheduleChildrenNodePathList
     * @param sortedIdList
     * @param idCountsList
     * @param scheduleBean
     */
    private void writeDataToNode(List<String> scheduleChildrenNodePathList, List<Long> sortedIdList,
                                   List<Integer> idCountsList, ScheduleBean scheduleBean) {
        int start = 0;
        int length;
        String impNodePath;
        for (int i = 0; i < scheduleChildrenNodePathList.size(); i++) {
            String impName = scheduleChildrenNodePathList.get(i);
            length = idCountsList.get(i);
            List<Long> dataList = sortedIdList.stream().skip(start).limit(length).collect(Collectors.toList());
            Long minId = dataList.stream().min(Long :: compareTo).orElse(0L);
            Long maxId = dataList.stream().max(Long :: compareTo).orElse(0L);
            distributeScheduleCuratorComponent.writeData2CurrentScheduleImplNodeGuaranteed(scheduleBean, minId + DistributeScheduleConstant.IDS_JOIN_CHARACTER + maxId);
            start = length;
        }
    }

    public void process(@NotNull String ids) {
        if (StringUtils.isEmpty(ids) || ids.indexOf(DistributeScheduleConstant.IDS_JOIN_CHARACTER) < 0) {
            log.info("schedule {} ids：{} is null or is not a legal ids data. ", this.getClass().getTypeName(), ids);
            return;
        }
        List<String> idArr = Arrays.asList(ids.split(DistributeScheduleConstant.IDS_JOIN_CHARACTER));
        if (idArr.size() != 2) {
            log.info("schedule {} ids：{} is not a legal ids data. ", this.getClass().getTypeName(), ids);
            return;
        }
        Long minId = Long.valueOf(idArr.get(0));
        Long maxId = Long.valueOf(idArr.get(1));
        try {
            this.doBusiness(minId, maxId);
        } finally {
            DistributeSchedule annotation = this.getClass().getAnnotation(DistributeSchedule.class);
            String serviceModuleName = annotation.value();
            String classFullName = this.getClass().getTypeName();
            Class clazz = this.getClass();
            ScheduleBean scheduleBean = new ScheduleBean(serviceModuleName, classFullName, clazz, null);
            distributeScheduleCuratorComponent.emptyCurrentScheduleImplNodeContentGuaranteed(scheduleBean);
        }
    }
}
