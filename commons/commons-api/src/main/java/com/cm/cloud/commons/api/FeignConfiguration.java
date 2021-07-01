package com.cm.cloud.commons.api;

import com.cm.cloud.commons.api.feign.CustomContract;
import feign.Contract;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * feign 公共配置
 */
@Configuration
@Slf4j
public class FeignConfiguration {

    @Autowired(required = false)
    private List<AnnotatedParameterProcessor> parameterProcessors = new ArrayList<>();

    @Autowired(required = false)
    private List<FeignFormatterRegistrar> feignFormatterRegistrars = new ArrayList<>();

    @Autowired(required = false)
    private List<HttpRequestInterceptor> requestInterceptors = new ArrayList<>();

    /**
     * 添加公共头信息
     *
     * @return
     */
    @Bean
    public CloseableHttpClient httpClient() {

        log.info("build custom httpClient");
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

        if (!CollectionUtils.isEmpty(requestInterceptors)) {
            requestInterceptors.forEach(interceptor -> httpClientBuilder.addInterceptorFirst(interceptor));
        }
        return httpClientBuilder.build();
    }


    @Bean
    public Contract feignContract() {
        return new CustomContract(this.parameterProcessors, feignConversionService());
    }

    public FormattingConversionService feignConversionService() {
        FormattingConversionService conversionService = new DefaultFormattingConversionService();
        for (FeignFormatterRegistrar feignFormatterRegistrar : feignFormatterRegistrars) {
            feignFormatterRegistrar.registerFormatters(conversionService);
        }
        return conversionService;
    }

//    @Bean
//    Request.Options feignOptions() {
//        return new Request.Options();
//    }
//
//    @Bean
//    Retryer feignRetryer() {
//        return Retryer.NEVER_RETRY;
//    }

    /**
     * 因为很多方法在api和servie中的地址完全一样
     * spring会解析feignClient中的RequestMapping 导致URL冲突 禁用spring解析FeignClient
     *
     * @return
     */
    @Bean
    @ConditionalOnClass({FeignClient.class})
    public WebMvcRegistrations registrations() {
        return new WebMvcRegistrations() {
            @Override
            public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
                return new FeignRequestMappingHandlerMapping();
            }
        };
    }

    private static class FeignRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
        @Override
        protected boolean isHandler(Class<?> beanType) {
            return super.isHandler(beanType) &&
                    !AnnotatedElementUtils.hasAnnotation(beanType, FeignClient.class);
        }
    }


}
