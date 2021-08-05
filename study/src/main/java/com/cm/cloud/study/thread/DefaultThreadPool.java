package com.cm.cloud.study.thread;


import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultThreadPool<Job extends Runnable> implements ThreadPool<Job>{
    //线程池最大数
    private static final int MAX_WORKER_NUM = 10;
    //线程池默认数
    private static final int DEFUALT_WORKER_NUM = 5;
    //线程池最小数
    private static final int MIN_WORKER_NUM = 1;
    //工作列表
    private final LinkedList<Job> jobs = new LinkedList<>();
    //工作者列表
    private final List<Worker> workers = Collections.synchronizedList(new ArrayList<>());
    //工作线程数
    private int workerNum = DEFUALT_WORKER_NUM;
    //线程编号
    private AtomicLong threadNum = new AtomicLong();

    public DefaultThreadPool(){
        initializeWokers(DEFUALT_WORKER_NUM);
    }

    public DefaultThreadPool(int num){
        workerNum = num > MAX_WORKER_NUM ? MAX_WORKER_NUM : num < MIN_WORKER_NUM ? MIN_WORKER_NUM : num;
        initializeWokers(num);
    }



    @Override
    public void execute(Job job) {
        if(job != null){
            synchronized (jobs){
                jobs.addLast(job);
                jobs.notify();
            }
        }
    }

    @Override
    public void shutdown() {
        workers.stream().forEach(worker -> {
            worker.shutdown();
        });
    }

    @Override
    public void addWorkers(int num) {
        synchronized (jobs){
            if(num + this.workerNum > MAX_WORKER_NUM){
                num = MAX_WORKER_NUM - this.workerNum;
            }
            initializeWokers(num);
            this.workerNum += num;
        }
    }

    @Override
    public void removeWorkers(int num) {
        synchronized (jobs){
            if(num >= this.workerNum){
                throw new IllegalArgumentException("beyond workNum");
            }
            //按照给定数量停止工作者
            int count = 0;
            while (count < num){
                Worker worker = workers.get(count);
                if(workers.remove(worker)){
                    worker.shutdown();
                    count ++;
                }
            }
            this.workerNum -= count;
        }
    }

    @Override
    public int getJobSize() {
        return jobs.size();
    }
    private void initializeWokers(int num) {
        for(int i = 0 ; i < num ; i ++){
            Worker worker = new Worker();
            workers.add(worker);
            Thread thread = new Thread(worker,"ChenMinThreadPool-Worker:" + threadNum.incrementAndGet());
            thread.start();
        }
    }

    /**
     * 工作者 负责消费任务
     */
    public class Worker implements Runnable{

        //是否工作
        private volatile boolean running = true;

        @Override
        public void run() {
            while (running){
                Job job = null;
                synchronized (jobs){
                    //工作列表是空的就等待别人给工作
                    while (jobs.isEmpty()){
                        try {
                            jobs.wait();
                        }catch (Exception e){
                            //感知外部中断操作，返回
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    job = jobs.removeFirst();
                }
                if(job != null){
                    try{
                        job.run();
                    }catch (Exception e){
//                        job执行异常
                    }
                }
            }
        }

        public void shutdown(){
            running = false;
        }

    }
}
