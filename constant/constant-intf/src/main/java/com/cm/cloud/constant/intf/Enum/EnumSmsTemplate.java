package com.cm.cloud.constant.intf.Enum;

import com.cm.cloud.commons.excption.ParameterNotValidException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum EnumSmsTemplate {

    LOGIN(0, "chenmin", "SMS_205432246", "验证码"),
    REGISTER(1, "chenmin", "SMS_205432246", "验证码"),
    FIND(2, "chenmin", "SMS_205432246", "验证码");

    private Integer type;
    private String sign;
    private String code;
    private String desc;

    public static EnumSmsTemplate valueOfType(Integer type) {

        return Stream.of(values())
                .filter(sms -> sms.getType().equals(type))
                .findAny().orElseThrow(() -> new ParameterNotValidException("短信类型不支持:" + type));
    }
}