package org.joe.cloud.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Tianze Zhu
 * @since 2022-06-11
 */
@Data
@ApiModel("移动文件VO")
public class MoveFileVo {
    @ApiModelProperty("用户文件id")
    private Long id;
    @ApiModelProperty("新路径")
    private String newPath;
}
