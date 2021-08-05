package com.cm.cloud.study.contants;

import com.alibaba.fastjson.JSONObject;

public class Study1Conf implements Runnable{

    static volatile Integer[] arr = {10,2,5,12,50,32,5,43,6,45,4,7,34,5623,52,542,35,236,1,3};
    static volatile boolean off = true;
    long count = 0;

    int a = 0;
    boolean flag = false;

    public void init(){
        a = 1;
        flag = true;
    }

    public void use(){
        System.out.println(a+"aaa"+flag);
        if(flag){
            int LocalA = a;
            if(LocalA == 0){
                System.out.println("111");
            }
        }
    }

    @Override
    public void run() {
//        while(off && !Thread.currentThread().isInterrupted()){
        while(off){
            count ++;
        }
        System.out.println(Thread.currentThread().getName()+"-:-"+count);
        System.out.println(Thread.currentThread().getName()+"-:-"+JSONObject.toJSONString(arr));
    }

    public void paixun(){
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - i -1; j++) {   // 这里说明为什么需要-1
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
        System.out.println("排序完："+JSONObject.toJSONString(arr));
        off = false;
    }
}
