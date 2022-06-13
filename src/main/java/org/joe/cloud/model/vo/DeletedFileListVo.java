package org.joe.cloud.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Tianze Zhu
 * @since 2022-06-12
 */
@Data
@ApiModel("回收站文件VO")
public class DeletedFileListVo {
    @ApiModelProperty(value = "当前页码", example = "1", notes = "页码从1开始")
    private Long currentPage;
    @ApiModelProperty(value = "一页显示数量", example = "10")
    private Long pageSize;
}
