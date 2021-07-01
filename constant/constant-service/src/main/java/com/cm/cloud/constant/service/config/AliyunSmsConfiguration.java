package com.cm.cloud.constant.service.config;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import lombok.Data;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Data
@Configuration
@ConfigurationProperties("aliyun.sms")
@Validated
public class AliyunSmsConfiguration {

    private static final String DOMAIN = "dysmsapi.aliyuncs.com";
    private static final String PRODUCT = "Dysmsapi";
    @NotNull
    private String accessKeyId;

    @NotNull
    private String accessKeySecret;

    @NotNull
    private String region;

    @NotNull
    private String endpoint;

    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean("smsClient")
    public IAcsClient sms() {

        IClientProfile profile = DefaultProfile.getProfile(region, accessKeyId, accessKeySecret);
        try {
            DefaultProfile.addEndpoint(endpoint, region, PRODUCT, DOMAIN);
        } catch (ClientException e) {
            e.printStackTrace();
            ;
            throw new IllegalArgumentException("初始化阿里endpoint失败");
        }
        return new DefaultAcsClient(profile);
    }

}
