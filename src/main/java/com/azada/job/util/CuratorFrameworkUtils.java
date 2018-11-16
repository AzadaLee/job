package com.azada.job.util;

import com.azada.job.config.TarsException;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class CuratorFrameworkUtils {

    @Resource
    private CuratorFramework curatorFramework;

    public String getNodeData(String path) {
        String data = "";
        try {
            byte[] byteData = curatorFramework.getData().forPath(path);
            if (null != byteData) {
                data = new String(byteData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 检查节点是否存在
     * @param nodePath
     * @return
     * true : 存在 ； false : 不存在
     */
    public boolean isNodeExists(String nodePath) {
        boolean flag = false;
        isLegalPath(nodePath);
        try {
            Stat stat = curatorFramework.checkExists().forPath(nodePath);
            if (null != stat) {
                flag = true;
            }
        } catch (Exception e) {
            log.error("check node path is exists error :{}",e);
            throw new TarsException("check node path is exists error");
        }
        return flag;
    }

    /**
     * 检查节点路劲是否合法
     * @param nodePath
     */
    private void isLegalPath(String nodePath) {
        if (StringUtils.isEmpty(nodePath) || !nodePath.startsWith("/")) {
            throw new TarsException("node path can not be null and must start with '/'");
        }
    }

    /**
     * 在不检查节点路劲是否存在的情况下创建临时节点
     * @param nodePath
     * @throws Exception
     */
    public void createNodeWithOutCheckExists(String nodePath) throws Exception {
        isLegalPath(nodePath);
        curatorFramework.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(nodePath);
    }

    /**
     * 获取子节点
     * @param parentNodePath
     * @return
     */
    public List<String> getChildrenNode(String parentNodePath) {
        List<String> childrenNode = null;
        if (isNodeExists(parentNodePath)) {
            try {
                childrenNode = curatorFramework.getChildren().forPath(parentNodePath);
            } catch (Exception e) {
                log.error("get children node error :{}", e);
            }
        }
        return childrenNode;
    }

    public void deleteNodePath(String nodePath) throws Exception {
        isLegalPath(nodePath);
        curatorFramework.delete().guaranteed().forPath(nodePath);
    }

    /**
     * 设置节点的值
     * @param nodePath
     * @param data
     * @throws Exception
     */
    public void setDataToNode(String nodePath, String data) {
        if (isNodeExists(nodePath)) {
            try {
                curatorFramework.setData().forPath(nodePath, data.getBytes());
            } catch (Exception e) {
                log.error("{} set data error: {}", nodePath, e);
            }
        }
    }
}
