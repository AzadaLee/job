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

    public String getNodeData(String path) throws Exception {
        String data = null;
        byte[] byteData = curatorFramework.getData().forPath(path);
        if (null != byteData) {
            data = new String(byteData);
        }
        return data;
    }

    /**
     * 检查节点是否存在
     * @param nodePath
     * @return
     * true : 存在 ； false : 不存在
     */
    public boolean isNodeExists(String nodePath) throws Exception {
        boolean exists = false;
        isLegalPath(nodePath);
        Stat stat = curatorFramework.checkExists().forPath(nodePath);
        if (null != stat) {
            exists = true;
        }
        return exists;
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
     * 在不检查节点路劲是否存在的情况下创建节点
     * @param nodePath
     * @throws Exception
     */
    public void createNodeWithOutCheckExists(String nodePath) throws Exception {
        isLegalPath(nodePath);
        curatorFramework.create().creatingParentContainersIfNeeded().forPath(nodePath);
    }

    /**
     * 在不检查节点路劲是否存在的情况下创建临时节点
     * @param nodePath
     * @throws Exception
     */
    public void createEphemeraLNodeWithOutCheckExists(String nodePath) throws Exception {
        isLegalPath(nodePath);
        curatorFramework.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(nodePath);
    }

    /**
     * 获取子节点
     * @param parentNodePath
     * @return
     */
    public List<String> getChildrenNode(String parentNodePath) throws Exception {
        List<String> childrenNode = null;
        if (isNodeExists(parentNodePath)) {
            try {
                childrenNode = curatorFramework.getChildren().forPath(parentNodePath);
            } catch (Exception e) {
                log.error("get children node error :{}", e);
                throw e;
            }
        }
        return childrenNode;
    }

    public void deleteNodePath(String nodePath) throws Exception {
        isLegalPath(nodePath);
        curatorFramework.delete().guaranteed().forPath(nodePath);
    }

    /**
     * @param nodePath
     * @param data
     * @return true：表示设置值成功；false：表示设置值失败
     */
    public boolean setDataToNode(String nodePath, String data) {
        //节点下是否可以设置值，true可以设置值，false不可以设置值
        boolean setDataAbleFlag = false;
        try {
            //isNodeExists 返回true代表目录存在，可以设置值，返回false代表目录不存在，不能设置
            setDataAbleFlag = isNodeExists(nodePath);
        } catch (Exception e) {
            //发生异常，默认不能设置值
            log.error("{} check node is exists error :{}", nodePath, e);
            return false;
        }
        if (setDataAbleFlag) {
            try {
                curatorFramework.setData().forPath(nodePath, data.getBytes());
                return true;
            } catch (Exception e) {
                log.error("{} set data error: {}", nodePath, e);
                return false;
            }
        }
        return false;
    }
}
