package com.cm.cloud.constant.intf.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EnumVcode {
    LOGIN(1,"登录"),
    BIND_PHONE(3, "绑定"),
    RETRIEVE_PASSWORD(2, "修改密码");

    private Integer type;

    private String desc;

    public Integer getPrefix(){
        return type;
    }

    public static EnumVcode findByType(Integer type){
        for(EnumVcode enumVcode:EnumVcode.values()){
            if(enumVcode.type.equals(type))
                return enumVcode;
        }
        return null;

    }
}
