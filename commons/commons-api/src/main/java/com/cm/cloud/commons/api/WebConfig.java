package com.cm.cloud.commons.api;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * web mvc 配置
 */
//@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configure cross origin requests processing.
     *
     * @param registry
     * @since 4.2
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
//        response.setHeader("Access-Control-Allow-Origin", "*");
//        response.setHeader("Access-Control-Allow-Methods", "POST, GET,PUT,PATCH, OPTIONS, DELETE");
//        response.setHeader("Access-Control-Max-Age", "3600");
//        response.setHeader("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Requested-With");
//        response.setHeader("Access-Control-Allow-Credentials", "true");
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedHeaders("Content-Type","Authorization","X-Requested-With")
                .allowedOrigins("*")
                .allowedMethods("POST","GET","PUT","PATCH","OPTIONS","DELETE")
                .maxAge(3600L);
    }
}
