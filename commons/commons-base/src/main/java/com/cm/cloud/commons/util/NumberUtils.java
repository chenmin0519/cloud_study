package com.cm.cloud.commons.util;


import com.cm.cloud.commons.constants.ApplicationConfig;

import java.util.Objects;

/**
 * Create By chenmin on 2018/2/1 11:27
 */
public class NumberUtils {

    public static boolean needCount(Integer count) {
        return (Objects.nonNull(count) && count.compareTo(ApplicationConfig.INT_ZERO) == 1);
    }

    public static boolean needCount(Long count) {
        return (Objects.nonNull(count) && count.compareTo(ApplicationConfig.LONG_ZERO) == 1);
    }
}
