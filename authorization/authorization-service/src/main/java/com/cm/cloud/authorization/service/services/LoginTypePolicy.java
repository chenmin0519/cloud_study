package com.cm.cloud.authorization.service.services;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.security.core.Authentication;

import java.util.Map;


public interface LoginTypePolicy {

    String LOGIN_TYPE_KEY = "login_type";


    /**
     * 授权顺序
     *
     * @return
     */
    default int sort() {
        return RandomUtils.nextInt(0,9521);
    }

    /**
     * 是否支持当前验证类型
     *
     * @param type
     * @return
     */
    boolean supports(Integer type);

    Authentication loadUser(String username, String password, Map<String, String> params);
}
