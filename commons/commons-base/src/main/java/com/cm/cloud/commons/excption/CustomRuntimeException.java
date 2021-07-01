package com.cm.cloud.commons.excption;

/**
 */
public class CustomRuntimeException extends RuntimeException {

    private Long errcode;

    private String message;

    public CustomRuntimeException(Long errcode, String message) {
        super(message);
        this.errcode = errcode;
        this.message = message;
    }

    public CustomRuntimeException(String message) {
        super(message);
        this.message = message;
    }

    /**
     * Fills in the execution stack trace. This method records within this
     * {@code Throwable} object information about the current state of
     * the stack frames for the current thread.
     * <p>
     * <p>If the stack trace of this {@code Throwable} {@linkplain
     * Throwable#Throwable(String, Throwable, boolean, boolean) is not
     * writable}, calling this method has no effect.
     *
     * @return a reference to this {@code Throwable} instance.
     * @see Throwable#printStackTrace()
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }

    /**
     * Returns the detail message string of this throwable.
     *
     * @return the detail message string of this {@code Throwable} instance
     * (which may be {@code null}).
     */
    @Override
    public String getMessage() {
        return message;
    }

    public Long getErrcode() {
        return errcode;
    }

    public void setErrcode(Long errcode) {
        this.errcode = errcode;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
