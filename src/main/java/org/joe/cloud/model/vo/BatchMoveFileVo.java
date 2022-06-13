package org.joe.cloud.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Tianze Zhu
 * @since 2022-06-12
 */
@Data
@ApiModel("批量移动VO")
public class BatchMoveFileVo {
    @ApiModelProperty(value = "用户文件列表DTO数组的JSON串", example = "[{\"id\":52,\"name\":\"admin\",\"path\":\"/\",\"extension\":null,\"updateTime\":\"2022-06-13 00:47:45\",\"size\":null,\"isFolder\":true,\"url\":null},{\"id\":51,\"name\":\"黑白棋题\",\"path\":\"/\",\"extension\":\"jpg\",\"updateTime\":\"2022-06-12 22:54:03\",\"size\":10783,\"isFolder\":false,\"url\":\"/upload/20220612/7f33c298541730124e498597e9e14002.jpg\"}]", notes = "后端用fastJSON解析")
    private String files;
    @ApiModelProperty(value = "新路径", example = "/a/", notes = "根目录 /")
    private String newPath;
}
