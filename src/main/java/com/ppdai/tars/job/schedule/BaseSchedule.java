package com.ppdai.tars.job.schedule;

import com.ppdai.tars.job.constant.CuratorConstant;
import com.ppdai.tars.job.util.CuratorFrameworkUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
@Slf4j
public abstract class BaseSchedule implements ISchedule{

    @Autowired
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
        List<String> scheduleChildrenNodePath = curatorFrameworkUtils.getChildrenNode(scheduleNodePath);
        if (!CollectionUtils.isEmpty(scheduleChildrenNodePath)) {

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
}
