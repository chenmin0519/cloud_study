package com.cm.cloud.commons.constants;

/**
 */
public interface ApplicationConfig {



    /**
     * feifn 调用时传入userID的headName
     */
    String FEIGN_HEADER_USER_ID = "f-user-id";

    String FEIGN_HTTP_HEADER_DATA_AUTHORITIES = "x-data-authorities";

    Integer INT_ZERO = Integer.valueOf(0);

    Long LONG_ZERO = Long.valueOf(0L);

}
