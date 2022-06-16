package org.joe.cloud.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Tianze Zhu
 * @since 2022-06-11
 */
@Data
@AllArgsConstructor
@ApiModel("下载文件VO")
public class DownloadFileVo {
    @ApiModelProperty("用户文件id")
    private Long id;
}
