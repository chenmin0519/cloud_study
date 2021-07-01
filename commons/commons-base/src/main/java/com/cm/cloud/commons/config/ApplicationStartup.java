package com.cm.cloud.commons.config;

import com.cm.cloud.commons.pojo.dto.NodeStatus;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 容器事件
 */
@Configuration
public class ApplicationStartup {

    @Value("${spring.application.name}")
    String serviceId;

    @Value("${spring.profiles}")
    String profiles;

    @Bean
    public ApplicationListener<ContextRefreshedEvent> refreshedEventApplicationListener() {

        return new ApplicationListener<ContextRefreshedEvent>() {
            @Override
            public void onApplicationEvent(ContextRefreshedEvent event) {

                try {
                    String hostAddress = InetAddress.getLocalHost().getHostAddress();
                    NodeStatus nodeStatus = new NodeStatus();
                    nodeStatus.setIp(hostAddress);
                    nodeStatus.setServiceId(serviceId);
                    nodeStatus.setState(1);
                    nodeStatus.setLastSuccess(LocalDateTime.now());
                    nodeStatus.setEnvironment(profiles);
                    String plainCredentials="monit:ailihan";
                    String base64Credentials = new String(Base64.encodeBase64(plainCredentials.getBytes()));
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Authorization", "Basic " + base64Credentials);
                    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                    RestTemplate restTemplate = new RestTemplate();
//                    restTemplate.exchange("http://192.168.19.22:8056/aitravel_node_status", HttpMethod.POST,new HttpEntity<Object>(nodeStatus,headers),NodeStatus.class);
                    //TODO save 2 db
                } catch (UnknownHostException e) {
                }

            }
        };
    }

    @Bean
    public ApplicationListener<ContextStartedEvent> startedEventApplicationListener() {

        return new ApplicationListener<ContextStartedEvent>() {

            @Override
            public void onApplicationEvent(ContextStartedEvent event) {
                System.err.println("ContextStartedEvent");
            }
        };
    }


}
