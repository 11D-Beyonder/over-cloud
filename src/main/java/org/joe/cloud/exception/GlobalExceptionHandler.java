package org.joe.cloud.exception;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.joe.cloud.common.RestResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Tianze Zhu
 * @since 2022-05-07
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 通用异常处理
     */
    @ExceptionHandler(Exception.class)
    public RestResponse error(Exception e) {
        log.error("全局异常捕获：" + e);
        return RestResponse.failure();
    }

    /**
     * 空指针异常处理
     */
    @ExceptionHandler(NullPointerException.class)
    public RestResponse nullPointerError(NullPointerException e) {
        log.error("全局异常捕获：", e);
        return RestResponse.failure("空指针异常");
    }

    /**
     * 下标越界处理
     */
    @ExceptionHandler(IndexOutOfBoundsException.class)
    public RestResponse indexOutOfBoundsError(IndexOutOfBoundsException e) {
        log.error("全局异常捕获：" + e);
        return RestResponse.failure("下标越界");
    }

    /**
     * 没有指定的文件类型
     */
    @ExceptionHandler(FileTypeNotFoundException.class)
    public RestResponse fileTypeNotFoundError(FileTypeNotFoundException e) {
        log.error("全局异常捕获：" + e);
        return RestResponse.failure("没有指定类型的文件");
    }

    /**
     * JWT错误
     */
    @ExceptionHandler(JwtException.class)
    public RestResponse jwtError(JwtException e) {
        log.error("全局异常捕获：" + e);
        log.warn("需要登录获得token");
        return RestResponse.failure("需要登录");
    }

    @ExceptionHandler(FileAlreadyExistsException.class)
    public RestResponse fileAlreadyExistsError(FileAlreadyExistsException e) {
        log.error("全局异常捕获：" + e);
        return RestResponse.failure(e.getMessage());
    }
}
