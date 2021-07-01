package com.cm.cloud.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EnumState {

    NORMAL(1,"正常"),
    FORBIDEN(1,"禁用"),
    ;

    private Integer key;

    private String desc;
}
