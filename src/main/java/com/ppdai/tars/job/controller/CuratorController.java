package com.ppdai.tars.job.controller;

import com.ppdai.tars.job.config.TarsException;
import com.ppdai.tars.job.controller.dto.NodeDataGetByNodePathReqDTO;
import com.ppdai.tars.job.controller.dto.ResponseDTO;
import com.ppdai.tars.job.util.CuratorFrameworkUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/curator")
public class CuratorController {

    @Autowired
    private CuratorFrameworkUtils curatorFrameworkUtils;

    @PostMapping("/getNodeDataByNodePath")
    public ResponseDTO getNodeDataByNodePath(@RequestBody NodeDataGetByNodePathReqDTO req) {
        String nodePath = req.getNodePath();
        if (StringUtils.isEmpty(nodePath)) {
            throw new TarsException("节点路劲不能为空");
        }
        if (!nodePath.startsWith("/")) {
            throw new TarsException("节点路劲必须以'/'开头");
        }
        return ResponseDTO.success(curatorFrameworkUtils.getNodeData(nodePath));
    }
}
