package com.cm.cloud.commons.excption;

/**
 */
public class ServiceLogicException extends CustomRuntimeException {

    public ServiceLogicException(Long errcode, String message) {
        super(errcode, message);
    }

    public ServiceLogicException(String message) {
        super(message);
    }
}
