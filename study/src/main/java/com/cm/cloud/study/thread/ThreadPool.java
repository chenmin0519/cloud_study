package com.cm.cloud.study.thread;

public interface ThreadPool<Job extends  Runnable> {

    /**
     * 执行job   job需要实现Runnable
     */
    void execute(Job job);

    /**
     * 关闭线程池
     */
    void shutdown();

    /**
     * 增加工作线程
     * @param num
     */
    void addWorkers(int num);

    /**
     * 减少工作线程
     * @param num
     */
    void removeWorkers(int num);

    /**
     * 获取等待执行任务数
     */
    int getJobSize();
}
