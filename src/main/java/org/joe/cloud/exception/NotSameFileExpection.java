package org.joe.cloud.exception;

/**
 * MD5校验失败异常
 *
 * @author Tianze Zhu
 * @since 2022-04-22
 */
public class NotSameFileExpection extends Exception {
    public NotSameFileExpection() {
        super("File MD5 Different");
    }
}
