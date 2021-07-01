package com.cm.cloud.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
    * @Description: TODO
    * @Author:      chenmin
    * @CreateDate:  2019-05-28 2019-05-28
    * @Version:     1.0
    * @JDK:         10
    */
@EnableZuulProxy
@SpringCloudApplication
public class APIGatewayApplication {


    public static void main(String[] args) {
        SpringApplication.run(APIGatewayApplication.class,args);
    }
}
