package com.cm.cloud.basic.service.util;

import com.cm.cloud.basic.intf.pojo.dto.WechatOpenidDTO;
import com.cm.cloud.basic.intf.pojo.dto.WeixinUserInfoDTO;
import com.cm.cloud.commons.excption.ServiceLogicException;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Configuration
@ConfigurationProperties("wechat.common")
@Data
public class WechatCommonClient {

    @NotNull
    private String appid;

    @NotNull
    private String secret;


    private static String URL_API = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code" +
            "=%s&grant_type=authorization_code";

    private static String URL_UNIONID = "https://api.weixin.qq" +
            ".com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";


    private RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    public void post() {
        //支持微信数据
        restTemplate.getMessageConverters().add(new WxMappingJackson2HttpMessageConverter());
    }

    /**
     * 小程序通过code换取openid
     *
     * @param code
     * @return
     */
    public WeixinUserInfoDTO code2UserInfo(String code) {
        WechatOpenidDTO forObject = restTemplate.getForObject(String.format(URL_API, appid, secret, code),
                WechatOpenidDTO.class);

        if (Objects.isNull(forObject.getErrcode()) || Long.valueOf(0).equals(forObject.getErrcode())) {

            WeixinUserInfoDTO userInfo = restTemplate.getForObject(String.format(URL_UNIONID, forObject
                    .getAccess_token(), forObject.getOpenid()), WeixinUserInfoDTO.class);
            return userInfo;
        }

        throw new ServiceLogicException("获取微信信息失败：" + forObject.getErrmsg());
    }

    static class WxMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
        public WxMappingJackson2HttpMessageConverter() {
            List<MediaType> mediaTypes = new ArrayList<>();
            mediaTypes.add(MediaType.TEXT_PLAIN);
            setSupportedMediaTypes(mediaTypes);//
        }
    }

}
