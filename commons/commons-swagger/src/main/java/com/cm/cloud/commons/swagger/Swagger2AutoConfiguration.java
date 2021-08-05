package com.cm.cloud.commons.swagger;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.*;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Configuration
@Profile({"dev","test"})
@ConditionalOnProperty(name = "swagger.enabled", matchIfMissing = true)
@EnableConfigurationProperties(SwaggerProperties.class)
public class Swagger2AutoConfiguration implements BeanFactoryAware {

    private static final Contact DEFAULT_CONTACT = new Contact("chenmin", "", "");

    private ConfigurableBeanFactory configurableBeanFactory;


    @Autowired
    SwaggerProperties swaggerProperties;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;
    }

    @Configuration
    @EnableSwagger2
    protected static class EnableSwagger {

    }


    @PostConstruct
    public void apis() {

        SwaggerProperties.OAuth2Properties auth2 = swaggerProperties.getOAuth2();

        swaggerProperties.getApis().forEach((key, value) -> {

            Docket docket = new Docket(DocumentationType.SWAGGER_2)
                    .groupName(key)
                    .genericModelSubstitutes(DeferredResult.class)
                    .useDefaultResponseMessages(false)
                    .forCodeGeneration(false)
                    .pathMapping("/")
                    .select()
                    .apis(RequestHandlerSelectors.basePackage(value.getPackageName()))
                    .paths(PathSelectors.any())//过滤的接口
                    .build()
                    .apiInfo(buildApiInfo(value));
            if (auth2.isEnable()) {
                docket.securitySchemes(Arrays.asList(oauth(swaggerProperties)));
                docket.securityContexts(Arrays.asList(securityContext(swaggerProperties)));
            }

            configurableBeanFactory.registerSingleton(key, docket);
        });

    }

    @Bean
    @ConditionalOnProperty(prefix = "swagger.o-auth2", value = "enable", matchIfMissing = true)
    public SecurityConfiguration securityInfo(SwaggerProperties swaggerProperties) {
        SwaggerProperties.OAuth2Properties oAuth2 = swaggerProperties.getOAuth2();
        return new SecurityConfiguration(oAuth2.getAuthClientId(), oAuth2.getAuthClientSecret(), oAuth2.getScopeCode(),
                oAuth2.getAppKey(), oAuth2.getAppKey(), ApiKeyVehicle.HEADER, "", ",");
    }

    @Bean
    @ConditionalOnProperty(prefix = "swagger.o-auth2", value = "enable", matchIfMissing = true)
    public SecurityContext securityContext(SwaggerProperties swaggerProperties) {
        SwaggerProperties.OAuth2Properties auth2 = swaggerProperties.getOAuth2();

        AuthorizationScope[] scopes = new AuthorizationScope[]{new AuthorizationScope(auth2.getScopeCode(),
                auth2.getScopeDesc())};

        SecurityReference securityReference = SecurityReference.builder().reference(auth2.getType()).scopes
                (scopes).build();

        List<SecurityReference> list = new ArrayList<>();
        list.add(securityReference);
        return SecurityContext.builder().securityReferences(list).forPaths(PathSelectors.any()).build();
    }

    @Bean
    @ConditionalOnProperty(value = "swagger.oAuth2.enable", matchIfMissing = true)
    SecurityScheme oauth(SwaggerProperties swaggerProperties) {

        SwaggerProperties.OAuth2Properties auth2 = swaggerProperties.getOAuth2();
        return new OAuthBuilder()
                .name(auth2.getType())
                .grantTypes(grantTypes(auth2.getAuthorizationUrl()))
                .scopes(scopes(auth2.getScopeCode(), auth2.getScopeDesc()))
                .build();
    }

    List<AuthorizationScope> scopes(String code, String desc) {

        List<AuthorizationScope> list = new ArrayList();
        list.add(new AuthorizationScope(code, desc));
        return list;
    }

    List<GrantType> grantTypes(String authorizationUrl) {
        List<GrantType> grants = new ArrayList();
        GrantType grantType = new ImplicitGrantBuilder()
                .loginEndpoint(new LoginEndpoint(authorizationUrl))
                .build();
        grants.add(grantType);
        return grants;
    }

    /**
     * 构造APIINFO
     */
    private ApiInfo buildApiInfo(SwaggerProperties.ApiInfoProperties apiInfoProperties) {

        return new ApiInfoBuilder()
                .description(apiInfoProperties.getDescription())
                .title(apiInfoProperties.getTitle())
                .contact(Optional.ofNullable(apiInfoProperties.getAuthor())
                        .map(author -> new Contact(author.getName(), author.getUrl(), author.getEmail()))
                        .orElse(DEFAULT_CONTACT))
                .license(apiInfoProperties.getLicense())
                .licenseUrl(apiInfoProperties.getLicenseUrl())
                .termsOfServiceUrl(apiInfoProperties.getTermsOfServiceUrl())
                .version(apiInfoProperties.getVersion())
                .build();
    }
}
