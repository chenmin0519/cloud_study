package com.cm.cloud.commons.swagger;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Map;

@ConfigurationProperties("swagger")
@Data
public class SwaggerProperties {

    private Boolean enable = false;
    /**
     * api配置
     */
    private Map<String, ApiInfoProperties> apis;

    @NestedConfigurationProperty
    private OAuth2Properties oAuth2 = new OAuth2Properties();

    @Data
    public static class OAuth2Properties {

        private boolean enable = false;

        private String type = "oauth2";

        private String authorizationUrl = "http://localhost:8830/se/oauth/authorize";

        private String authClientId = "swagger";

        private String authClientSecret = "swagger";

        /**
         * 授权信息
         */
        private String scopeCode = "openid";
        /**
         * 描述
         */
        private String scopeDesc = "用户信息";

        /**
         * 应用id
         */
        private String appKey;

        /**
         * 应用名称
         */
        private String appName;
    }

    @Data
    public static class ApiInfoProperties {

        /**
         * 作者
         */
        private Author author;

        /**
         * 分组名
         */
        private String group;

        /**
         * 包名
         */
        private String packageName;

        /**
         * 描述
         */
        private String description = "swagger api documentation";

        /**
         * 标题
         **/
        private String title = "Api Documentation";

        /**
         * 版本
         **/
        private String version = "SNAPSHOT";
        /**
         * 许可证
         **/
        private String license = "Apache 2.0";
        /**
         * 许可证URL
         **/
        private String licenseUrl = "http://www.apache.org/licenses/LICENSE-2.0";
        /**
         * 服务条款URL
         **/
        private String termsOfServiceUrl = "urn:tos";

    }

    @Data
    public static class Author {
        private String name;
        private String url;
        private String email;
    }
}
