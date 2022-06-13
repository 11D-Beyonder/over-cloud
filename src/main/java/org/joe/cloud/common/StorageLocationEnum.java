package org.joe.cloud.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Joe
 * @since 2022-04-22
 */
@Getter
@AllArgsConstructor
public enum StorageLocationEnum {
    // 存储的位置
    LOCAL(0, "本地存储"),
    ADRIVE(1, "阿里云盘");

    private int code;
    private String note;
}