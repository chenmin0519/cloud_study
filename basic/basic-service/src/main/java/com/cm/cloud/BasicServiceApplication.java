package com.cm.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableTransactionManagement
@SpringCloudApplication
@EnableSwagger2
public class BasicServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BasicServiceApplication.class, args);
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    @Bean
    @Profile({"dev", "test"})
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).groupName("basic").apiInfo(apiInfo()).select()
                .apis(RequestHandlerSelectors.basePackage("com.cm.cloud.basic.service.controller"))
                .paths(PathSelectors.any())// 过滤的接口
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("cm-data").description("cmnm").termsOfServiceUrl("/")
                .contact(new Contact("cm", "", "")).license("cmm").licenseUrl("").version
                        ("1.0").build();
    }
}
