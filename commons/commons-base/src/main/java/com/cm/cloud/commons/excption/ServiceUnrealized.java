package com.cm.cloud.commons.excption;

/**
 */
public class ServiceUnrealized extends CustomRuntimeException {
    private static final Long ERRCODE_MIN = 4000_000L;

    public ServiceUnrealized(String message) {
        super(ERRCODE_MIN, message);
    }


    public ServiceUnrealized(Long errcode, String message) {
        super(ERRCODE_MIN + errcode, message);
    }
}
