package com.cm.cloud.commons.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 */
public final class RegUtils {

    public static boolean isPhone(String phone) {
        if (StringUtils.isBlank(phone))
            return true;
        //1 开的头11位 。。。
        return Pattern.matches("^1[0-9]{10}$", phone);
    }

}
