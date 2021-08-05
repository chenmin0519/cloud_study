package com.cm.cloud.study.concurrent;

import java.util.ArrayList;
import java.util.List;

public class StudyThread {

    private static volatile boolean notStart = true;

    private static volatile boolean notEnd = true;

    public static void main(String[] args) throws InterruptedException {
        while (true){
            Thread.sleep(1000);
        }
    }
    public static void main1(String[] args) throws InterruptedException {
        List<Job> jobs = new ArrayList<>();
        for (int i= 0 ; i < 10 ; i ++){
            int priority = i < 5 ? Thread.MIN_PRIORITY : Thread.MAX_PRIORITY;
            Job job = new Job(priority);
            jobs.add(job);
            Thread thread = new Thread(job,"Thread:"+i);
            thread.setPriority(priority);
            thread.start();
        }
        notStart = false;
        Thread.sleep(1000);
        notEnd = false;
        jobs.forEach(job -> {
            System.out.println(" jobPriority:" + job.priority + "    count:" + job.count );
        });
    }

    static class Job implements Runnable{
        private int priority;
        private long count = 0;
        Job(int priority){
            this.priority = priority;
        }

        @Override
        public void run() {
            while (notStart){
                    Thread.yield();
            }
            while (notEnd){
                Thread.yield();
                count ++;
            }
            System.out.println(Thread.currentThread().getName() + "次数：" + count);
        }
    }
}
