package com.cm.cloud.basic.intf.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


@ApiModel("微信登录")
@Data
public class WechatLoginVO {


    @NotNull
    @ApiModelProperty(value = "授权code", required = true)
    private String code;

    @ApiModelProperty("昵称如果有")
    private String nickName;

    @ApiModelProperty("头像 如果有")
    private String avatarUrl;

    @ApiModelProperty("MINI-小程序，ANDROID-安卓，IOS-IOS")
    private String source;

    @ApiModelProperty("小程序appid区分多个")
    private String appid;

    private String unionid;
}
