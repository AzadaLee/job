package com.azada.job.controller.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author taoxiuma
 */
@Data
public class NodeDataGetByNodePathReqDTO implements Serializable {

    private static final long serialVersionUID = -632603922492205529L;

    /**
     * 节点路劲
     */
    private String nodePath;

}
