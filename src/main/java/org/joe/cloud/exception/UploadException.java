package org.joe.cloud.exception;

/**
 * 上传异常
 *
 * @author Joe
 * @since 2022-04-22
 */
public class UploadException extends RuntimeException {
    public UploadException(Throwable cause) {
        super("上传出现了异常", cause);
    }

    public UploadException(String message) {
        super(message);
    }

    public UploadException(String message, Throwable cause) {
        super(message, cause);
    }
}