package com.azada.job.constant;

public class DistributeScheduleConstant {

    /**
     * zookeeper lock node
     */
    public static final String NODE_LOCK = "LOCK";

    /**
     * zookeeper schedule node
     */
    public static final String NODE_SCHEDULE  = "SCHEDULE";

    /**
     * 文件夹开始符
     */
    public static final String DIRECTORY_CHARACTER = "/";

    /**
     * 往zookeeper写id连接符
     */
    public static final String IDS_JOIN_CHARACTER = "-";

    /**
     * 空内容
     */
    public static final byte[] EMPTY_DATA = null;
}
