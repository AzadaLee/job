package com.azada.job.constant;

/**
 * @author taoxiuma
 */
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

    /**
     * 定时任务开关属性名
     */
    public static final String SCHEDULE_SWITCHER_FIELD = "SWITCHER";

    /**
     * 定时任务开关默认值
     */
    public static final String SCHEDULE_SWITCHER_DEFAULT_VALUE = "ON";

    /**
     * 默认命名空间
     */
    public static final String DEFAULT_NAMESPACE = "DEFAULT";
}
