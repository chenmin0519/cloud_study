package com.cm.cloud.comons.intf.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StatusEnum {
    YES(1,"正常"),
    NO(0,"删除"),
    D(2,"禁用");;
    public static String desc = "状态类型";
    private Integer key;
    private String value;
}
