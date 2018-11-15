package com.ppdai.tars.job.util;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CuratorFrameworkUtils {

    @Autowired
    private CuratorFramework curatorFramework;

    public String getNodeData(String path) {
        String data = "";
        try {
            byte[] byteData = curatorFramework.getData().forPath(path);
            data = new String(byteData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

}
