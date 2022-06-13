package org.joe.cloud.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Tianze Zhu
 * @since 2022-05-09
 */
@Data
@ApiModel("删除文件VO")
public class DeleteFileVo {
    @ApiModelProperty(value = "用户文件id", example = "12", required = true)
    private Long id;
    @ApiModelProperty(value = "文件路径", notes = "已弃用")
    @Deprecated
    private String path;
    @ApiModelProperty(value = "文件名", notes = "已弃用")
    @Deprecated
    private String name;
    @ApiModelProperty(value = "是否是目录", notes = "已弃用")
    @Deprecated
    private Integer isFolder;
}
