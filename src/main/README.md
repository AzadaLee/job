# 说明
## 使用
* properties文件中加上curator.zookeeper.address和curator.zookeeper.nameSpace属性
* curator.zookeeper.address为zk地址，集群形式：10.114.24.188:2181,10.114.24.189:2181,10.114.24.190:2181
* curator.zookeeper.nameSpace用于资源分割，可以是应用的名称
* 继承BaseDistributeSchedule类，重写void start(),List<Long> getIdList(),void doBusiness(Long minId, Long maxId)方法；
* 标注@DistributeSchedule；
* start()：启动调度（spring-quarz），并调用init()方法
* getIdList：待处理业务数据的id集合
* doBusiness：根据入参minId和maxId查询区间内的业务数据，并处理业务
## @DistributeSchedule说明
* 支持单节点执行：将singleNode属性设置为true（默认是false）

## 开关说明
* 所有的任务默认是开启的(on)，如需关闭，需要重写getSwitcher方法