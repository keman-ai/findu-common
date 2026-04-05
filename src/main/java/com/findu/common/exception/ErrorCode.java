package com.findu.common.exception;

/**
 * Error code contract. All service-specific error code enums must implement this.
 */
public interface ErrorCode {
    String getCode();
    String getMessage();
    String getHowto();
}
