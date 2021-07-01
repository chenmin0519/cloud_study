package com.cm.cloud.constant.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties("oss")
@Configuration
@Data
public class OssClientConfigurer {
    private String accessKeyId;
    private String accessKeySecret;
    /**
     * 用户的存储空间（bucket）名称
     */
    private  String bucketName;
    /**
     * 对应的映射域名
     */
    private  String accessUrl;
    /**
     * 用户的存储空间所在数据中心的访问域名
     */
    private  String endpoint;
    /**
     * 指定项目文件夹
     */
    private  String picLocation;

    /**
     * 加密密钥
     */
    private  String key;
}
