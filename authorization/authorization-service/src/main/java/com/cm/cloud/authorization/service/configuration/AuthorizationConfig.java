package com.cm.cloud.authorization.service.configuration;

import com.cm.cloud.authorization.service.services.CustomAuthenticationProvider;
import com.cm.cloud.authorization.service.services.CustomRedirectStrategy;
import com.cm.cloud.authorization.service.services.CustomUserDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;


@Configuration
@EnableResourceServer
@EnableAuthorizationServer
@EnableConfigurationProperties({AuthorizationProperties.class})
public class AuthorizationConfig extends AuthorizationServerConfigurerAdapter {


    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CustomUserDetailService userDetailsService;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    CustomAuthenticationProvider customAuthenticationProvider;

    @Autowired
    private AuthorizationProperties authorizationProperties;

    //spirng boot 2 不再为我们自动创建这个Bean
    @Bean
    public ProviderManager authenticationManager() {
        return new ProviderManager(Arrays.asList(customAuthenticationProvider));
    }

    @Bean
    public TokenStore tokenStore() {
        return new CustomRedisTokenStore(this.redisConnectionFactory);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//        clients.jdbc(dataSource);
        InMemoryClientDetailsServiceBuilder inMemoryClientDetailsServiceBuilder = clients.inMemory();
        authorizationProperties.getClients().forEach((key, value) -> {
            try {
                inMemoryClientDetailsServiceBuilder.withClient(key)
                        .secret(value.getClientSecret())
                        .authorizedGrantTypes(Optional.ofNullable(value.getAuthorizedGrantTypes()).orElse("").split
                                (","))
                        .authorities(Optional.ofNullable(value.getAuthorities()).orElse("").split(","))
                        .scopes(Optional.ofNullable(value.getScope()).orElse("").split(","))
                        .resourceIds(Optional.ofNullable(value.getResourceIds()).orElse("").split(","))
                        .accessTokenValiditySeconds(value.getAccessTokenValiditySeconds())
                        .refreshTokenValiditySeconds(value.getRefreshTokenValiditySeconds())
                        .autoApprove(value.getAutoapprove());
            } catch (Exception e) {
                logger.error("初始化ClientDetailsServiceConfigurer失败", e);
            }
        });
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

        endpoints.tokenStore(this.tokenStore()).userDetailsService(userDetailsService)
                //.authorizationCodeServices(authorizationCodeServices())// InMemoryAuthorizationCodeServices default
                .authenticationManager(this.authenticationManager())
                .approvalStoreDisabled()
                //RefreshTokens 使用一次就重置
                .reuseRefreshTokens(false);
    }

    @Order(-22)
    @Configuration
    protected static class ResourceServer extends WebSecurityConfigurerAdapter {


        @Value("${server.context-parameters.gateway-path:}")
        private String gatewayPath;


        @Bean
        public SavedRequestAwareAuthenticationSuccessHandler savedRequestAwareAuthenticationSuccessHandler() {

            SavedRequestAwareAuthenticationSuccessHandler auth = new SavedRequestAwareAuthenticationSuccessHandler();
            CustomRedirectStrategy redirectStrategy = new CustomRedirectStrategy();
            redirectStrategy.setUrl(gatewayPath);
            redirectStrategy.setContextRelative(true);
            auth.setRedirectStrategy(redirectStrategy);
            auth.setDefaultTargetUrl(gatewayPath);
            return auth;
        }

        /**
         * Override this method to configure {@link WebSecurity}. For example, if you wish to
         * ignore certain requests.
         *
         * @param web
         */
        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().requestMatchers(EndpointRequest.to("info", "health"));
        }

        @Autowired
        private AuthenticationManager authenticationManager;

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http.authorizeRequests()// ACTUATOR 的配置
                    .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ACTUATOR")
                    .requestMatchers().permitAll()
                    .and()
                    .formLogin().loginPage("/login").successForwardUrl(gatewayPath).permitAll()
                    .successHandler(savedRequestAwareAuthenticationSuccessHandler()).failureForwardUrl(gatewayPath)
                    .and()
                    .logout().logoutSuccessUrl(gatewayPath).permitAll().and()
                    .requestMatchers()
                    .antMatchers("/login", "/oauth/authorize", "/oauth/confirm_access")
                    .and().authorizeRequests()
                    .anyRequest().authenticated();
            if (!StringUtils.isEmpty(gatewayPath)) {
                http.formLogin().loginPage(gatewayPath + "login");
            }
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {

            auth.parentAuthenticationManager(authenticationManager);
        }


    }
}
