package com.cm.cloud.study.concurrent;

import com.alibaba.fastjson.JSONObject;
import com.cm.cloud.study.contants.Study1Conf;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

public class Study1 {

    static volatile Integer a = 5;

    public static void main(String[] args) throws Exception {
        Study1 study1 = new Study1();
        study1.mthed2();
    }

    private void mthed2() throws InterruptedException {
        Study1Conf t1 = new Study1Conf();
        Thread thread1 = new Thread(t1,"myThread1");
        thread1.start();
        TimeUnit.SECONDS.sleep(1);
        thread1.interrupt();

        Study1Conf t2 = new Study1Conf();
        Thread thread2 = new Thread(t2,"myThread2");
        thread2.start();
        TimeUnit.SECONDS.sleep(1);
        t2.paixun();
    }

    private void mthed1() {
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                getStudyConf().init();
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0 ; i < 10;i++) {
                    getStudyConf().use();
                }
            }
        });
        thread1.start();
        thread2.start();
    }

    private static Study1Conf study1Conf;

    public static Study1Conf getStudyConf(){
        if(study1Conf == null){
            synchronized (Study1.class){
                if(study1Conf == null)
                    study1Conf = new Study1Conf();
            }
        }
        return study1Conf;
    }
}

    //[1,2,3,4,5,5,6,7,10,12,32,34,35,43,45,50,52,236,542,5623]
    //[1,2,3,4,5,5,6,7,10,12,32,34,35,43,45,50,52,236,542,5623]
    //[1,2,3,4,5,5,6,7,10,12,32,34,35,43,45,50,52,236,542,5623]
