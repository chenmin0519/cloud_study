package com.cm.cloud.authorization.service.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Map;


@ConfigurationProperties("oauth2")
public class AuthorizationProperties {
    @NestedConfigurationProperty
    private Map<String, OAuth2Client> clients;

    public Map<String, OAuth2Client> getClients() {
        return clients;
    }

    public void setClients(Map<String, OAuth2Client> clients) {
        this.clients = clients;
    }

    @Data
    public static class OAuth2Client {
        private String resourceIds;
        private String clientSecret;
        private String scope = "openid";
        private String authorizedGrantTypes;
        private String authorities = "";
        private int accessTokenValiditySeconds = 604800;
        private int refreshTokenValiditySeconds = 1209600;
        private String additionalInformation = "{}";
        private Boolean autoapprove = Boolean.TRUE;
    }
}
