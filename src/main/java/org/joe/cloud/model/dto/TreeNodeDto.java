package org.joe.cloud.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tianze Zhu
 * @since 2022-05-10
 */
@Data
@ApiModel("文件树结点")
public class TreeNodeDto {
    @ApiModelProperty(value = "结点名", example = "test")
    private String label;

    @ApiModelProperty(value = "深度", example = "2", notes = "深度从1开始")
    private Long depth;

    @ApiModelProperty(value = "文件夹内文件的路径", example = "/test/", notes = "根目录 /")
    private String filePath;

    @ApiModelProperty(value = "子节点列表", notes = "又是TreeNode")
    private List<TreeNodeDto> children = new ArrayList<>();
}