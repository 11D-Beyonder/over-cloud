package org.joe.cloud.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Tianze Zhu
 * @since 2022-05-10
 */
@Data
@ApiModel("重命名文件VO")
public class RenameFileVo {
    @ApiModelProperty(value = "用户文件ID", example = "1")
    private Long id;
    @ApiModelProperty(value = "文件名", example = "test")
    private String name;
    @ApiModelProperty(value = "后缀名", example = "txt")
    private String extension;
    @ApiModelProperty(value = "是否为文件夹", example = "false")
    private Boolean isFolder;
}
