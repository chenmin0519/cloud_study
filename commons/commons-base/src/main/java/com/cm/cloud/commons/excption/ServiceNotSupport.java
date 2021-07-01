package com.cm.cloud.commons.excption;

/**
 */
public class ServiceNotSupport extends CustomRuntimeException {

    private static final Long ERRCODE_MIN = 3000_000L;

    public ServiceNotSupport(Long errcode, String message) {
        super(ERRCODE_MIN + errcode, message);
    }

    public ServiceNotSupport(String message) {
        super(ERRCODE_MIN, message);
    }
}
