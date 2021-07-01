package com.cm.cloud.authorization.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum EnumLoginMode {
    AUTO("自动"),
    HAND("手动");

    private String desc;
}
