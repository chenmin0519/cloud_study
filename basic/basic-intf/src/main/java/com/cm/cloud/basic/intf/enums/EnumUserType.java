package com.cm.cloud.basic.intf.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum EnumUserType {

    APP(1, "手机用户"),
    ADMIN(2, "后台用户");

    private Integer key;

    private String desc;
}
