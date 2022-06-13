package org.joe.cloud.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Tianze Zhu
 * @since 2022-05-06
 */
@Data
@ApiModel("创建文件夹")
public class CreateFolderVo {
    @ApiModelProperty(name = "文件夹名", example = "test", notes = "在前端校验：不能有特殊符号，字符串开头和末尾不能有空格。")
    private String name;
    @ApiModelProperty(name = "文件路径", example = "/test/a/b/")
    private String path;
}
