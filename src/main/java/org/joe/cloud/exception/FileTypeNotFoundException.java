package org.joe.cloud.exception;

/**
 * @author Tianze Zhu
 * @since 2022-05-07
 */
public class FileTypeNotFoundException extends RuntimeException {
    public FileTypeNotFoundException(String message) {
        super(message);
    }
}
