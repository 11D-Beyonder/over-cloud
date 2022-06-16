package org.joe.cloud.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Tianze Zhu
 * @since 2022-06-12
 */
@Data
@ApiModel("回收站文件DTO")
public class DeletedFileDto {
    @ApiModelProperty(value = "用户文件ID", example = "2")
    private Long id;
    @ApiModelProperty(value = "文件名", example = "hello")
    private String name;
    @ApiModelProperty(value = "扩展名", example = "txt")
    private String extension;
    @ApiModelProperty(value = "删除时间", example = "2022-06-12 20:35:44")
    private String deleteTime;
    @ApiModelProperty(value = "文件大小", example = "23")
    private Long size;
    @ApiModelProperty(value = "是否是目录", example = "false")
    private Boolean isFolder;
    @ApiModelProperty(value = "原位置", example = "/text/")
    private String path;
}
