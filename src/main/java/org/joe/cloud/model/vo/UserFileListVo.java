package org.joe.cloud.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Tianze Zhu
 * @since 2022-05-07
 */
@Data
@ApiModel("文件列表VO")
public class UserFileListVo {
    @ApiModelProperty(value = "文件路径", example = "/test/", notes = "根目录为 /")
    private String path;
    @ApiModelProperty(value = "文件类型", example = "1", notes = "0-全部 1-相册 2-档案库 3-放映室 4-音乐馆")
    private Integer type;
    @ApiModelProperty(value = "当前页码", example = "2", notes = "页码从1开始")
    private Long currentPage;
    @ApiModelProperty(value = "一页显示数量", example = "10")
    private Long pageSize;
}
