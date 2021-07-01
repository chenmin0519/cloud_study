package com.cm.cloud.comons.intf.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserTypeEnum {
    MINI_APP("MINI_APP","小程序"),
    MOBILE("MOBILE","手机注册");
    public static String desc = "用户来源";
    private String key;
    private String value;
}
