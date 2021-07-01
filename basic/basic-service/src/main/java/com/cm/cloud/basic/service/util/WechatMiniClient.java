package com.cm.cloud.basic.service.util;

import com.cm.cloud.basic.intf.pojo.dto.WechatOpenidDTO;
import com.cm.cloud.commons.excption.ParameterNotValidException;
import com.cm.cloud.commons.excption.ServiceLogicException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Configuration
@ConfigurationProperties("wechat.mini")
@Validated
@Data
@Slf4j
public class WechatMiniClient {

    private String defaultAppid;

    private Appinfo defaulApp;

    @NotEmpty
    private Map<String, Appinfo> apps;
    private static String api = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code" +
            "=%s&grant_type=authorization_code";


    private RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    public void post() {

        apps.forEach((key, info) -> {
            if (StringUtils.isBlank(info.getAppid())) {
                info.setAppid(key);
            }
        });
        //支持微信数据
        restTemplate.getMessageConverters().add(new WxMappingJackson2HttpMessageConverter());

        if (StringUtils.isNotBlank(defaultAppid)) {
            if (apps.containsKey(defaultAppid)) {
                defaulApp = apps.get(defaultAppid);
                log.info("配置默认appid为:" + defaulApp);
            } else {
                throw new ParameterNotValidException("apps not contain defaulAppid :" + defaultAppid);
            }
        } else {
            defaulApp = apps.entrySet().stream().findFirst().get().getValue();
        }

    }

    /**
     * 小程序通过code换取openid
     *
     * @param code
     * @return
     */
    public WechatOpenidDTO jscode2session(String code) {

        return jscode2session(code, null);
    }

    /**
     * 小程序通过code换取openid
     *
     * @param code
     * @return
     */
    public WechatOpenidDTO jscode2session(String code, String appid) {

        Appinfo appinfo = defaulApp;
        if (StringUtils.isNotBlank(appid)) {

            if (apps.containsKey(appid)) {
                appinfo = apps.get(appid);
            } else {
                log.warn("小程序appid未配置:" + appid + ",使用默认appid:" + defaulApp.getAppid());
//                throw new ParameterNotValidException("小程序appid未配置");
            }
        }
        WechatOpenidDTO forObject = restTemplate.getForObject(String.format(api, appinfo.getAppid(), appinfo
                        .getSecret(), code),
                WechatOpenidDTO.class);

        if (Objects.isNull(forObject.getErrcode()) ||
                Long.valueOf(0).equals(forObject.getErrcode()))
            return forObject;

        throw new ServiceLogicException("获取微信信息失败：" + forObject.getErrmsg());
    }

    @Data
    public static class Appinfo {

        /**
         * The ID of the info (the same as its map key by default).
         */
        @NotNull
        private String appid;

        @NotNull
        private String secret;
    }

    static class WxMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
        public WxMappingJackson2HttpMessageConverter() {
            List<MediaType> mediaTypes = new ArrayList<>();
            mediaTypes.add(MediaType.TEXT_PLAIN);
            setSupportedMediaTypes(mediaTypes);//
        }
    }

}
