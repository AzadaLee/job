package com.ppdai.tars.job.schedule;

import com.ppdai.tars.job.constant.CuratorConstant;
import com.ppdai.tars.job.util.CuratorFrameworkUtils;
import com.ppdai.tars.job.util.ScheduleUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public abstract class BaseSchedule implements ISchedule{

    @Resource
    private CuratorFrameworkUtils curatorFrameworkUtils;

    public void init() {
        String classFullName = this.getClass().getTypeName();
        //acquire lock
        String lockNodePath = CuratorConstant.DIRECTORY_CHARACTER.concat(CuratorConstant.NODE_LOCK)
                .concat(CuratorConstant.DIRECTORY_CHARACTER).concat(classFullName);
        try {
            acquireLock(lockNodePath);
        } catch (Exception e) {
            log.info("{} acquire lock fail, schedule is doing ", classFullName);
            return;
        }
        //acquire imp schedule children node
        String scheduleNodePath = CuratorConstant.DIRECTORY_CHARACTER.concat(CuratorConstant.NODE_SCHEDULE)
                .concat(CuratorConstant.DIRECTORY_CHARACTER).concat(classFullName);
        List<String> scheduleChildrenNodePathList;
        try {
            scheduleChildrenNodePathList = curatorFrameworkUtils.getChildrenNode(scheduleNodePath);
        } catch (Exception e){
            // release lock
            deleteNodePath(lockNodePath);
            return;
        }
        if (!CollectionUtils.isEmpty(scheduleChildrenNodePathList)) {
            List<Integer> idList = this.getIdList();
            if (CollectionUtils.isEmpty(idList)) {
                log.info("{} has no data , schedule should not execute", classFullName);
                deleteNodePath(lockNodePath);
                return;
            }
            //sort
            idList = idList.stream().sorted().collect(Collectors.toList());
            List<Integer> idCountsList = ScheduleUtil.average(idList.size(), scheduleChildrenNodePathList.size());
            //write data to node
            writeDataToNode(scheduleChildrenNodePathList, idList, idCountsList, scheduleNodePath);
        } else {
            deleteNodePath(lockNodePath);
        }
    }

    /**
     * 往应用实例节点写数据
     * @param scheduleChildrenNodePathList
     * @param sortedIdList
     * @param idCountsList
     * @param baseNodePath
     */
    private void writeDataToNode (List<String> scheduleChildrenNodePathList, List<Integer> sortedIdList,
                                   List<Integer> idCountsList, String baseNodePath) {
        int start = 0;
        int length;
        String impNodePath;
        for (int i = 0; i < scheduleChildrenNodePathList.size(); i++) {
            String impName = scheduleChildrenNodePathList.get(i);
            length = idCountsList.get(i);
            impNodePath = baseNodePath.concat(CuratorConstant.DIRECTORY_CHARACTER).concat(impName);
            List<Integer> dataList = sortedIdList.stream().skip(start).limit(length).collect(Collectors.toList());
            Integer minId = dataList.stream().min(Integer :: compareTo).orElse(0);
            Integer maxId = dataList.stream().max(Integer :: compareTo).orElse(0);
            curatorFrameworkUtils.setDataToNode(impNodePath, minId + CuratorConstant.IDS_JOIN_CHARACTER + maxId);
            start = length;
        }
    }
    /**
     * acquire lock
     * if there is no exception that acquire lock success
     * @param nodePath
     * @throws Exception
     */
    private void acquireLock(String nodePath) throws Exception {
        curatorFrameworkUtils.createNodeWithOutCheckExists(nodePath);
    }

    /**
     * delete node path guaranteed
     * @param nodePath
     */
    private void deleteNodePath(String nodePath) {
        try {
            if (!curatorFrameworkUtils.isNodeExists(nodePath)) {
                curatorFrameworkUtils.deleteNodePath(nodePath);
            }
        } catch (Exception e) {
            log.error("release {} lock error :{}", nodePath, e);
        }
    }

    /**
     *
     * @param lockNodePath
     * @param serviceNodePath
     */
    private void tryReleaseLock(String lockNodePath, String serviceNodePath) {

    }
}
