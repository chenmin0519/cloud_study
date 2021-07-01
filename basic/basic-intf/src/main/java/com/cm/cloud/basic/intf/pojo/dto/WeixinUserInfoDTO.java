package com.cm.cloud.basic.intf.pojo.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("微信用户信息")
public class WeixinUserInfoDTO {

    private String openid;

    private String nickname;

    private String sex;

    private String province;

    private String city;

    private String headimgurl;

    private List<String> privilege;

    private String unionid;

    private String sessionKey;

}
