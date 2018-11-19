package com.azada.job.controller;

import com.azada.job.config.TarsException;
import com.azada.job.constant.DistributeScheduleConstant;
import com.azada.job.controller.dto.NodeDataGetByNodePathReqDTO;
import com.azada.job.controller.dto.ResponseDTO;
import com.azada.job.util.CuratorFrameworkUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author taoxiuma
 */
@RestController
@RequestMapping("/curator")
public class CuratorController {

    @Resource
    private CuratorFrameworkUtils curatorFrameworkUtils;

    @PostMapping("/getNodeDataByNodePath")
    public ResponseDTO getNodeDataByNodePath(@RequestBody NodeDataGetByNodePathReqDTO req) throws Exception {
        String nodePath = req.getNodePath();
        if (StringUtils.isEmpty(nodePath)) {
            throw new TarsException("节点路劲不能为空");
        }
        if (!nodePath.startsWith(DistributeScheduleConstant.DIRECTORY_CHARACTER)) {
            throw new TarsException("节点路劲必须以'/'开头");
        }
        return ResponseDTO.success(curatorFrameworkUtils.getNodeData(nodePath));
    }

    @PostMapping("/deleteNodePath")
    public ResponseDTO deleteNodePath (@RequestBody NodeDataGetByNodePathReqDTO req) {
        String nodePath = req.getNodePath();
        try {
            curatorFrameworkUtils.deleteNodePath(nodePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseDTO.success();
    }
}
