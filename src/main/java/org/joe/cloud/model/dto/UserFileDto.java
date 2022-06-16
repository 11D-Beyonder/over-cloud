package org.joe.cloud.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Tianze Zhu
 * @since 2022-05-07
 */
@Data
@ApiModel("用户文件列表DTO")
public class UserFileDto {
    @ApiModelProperty("文件ID")
    private Long id;
    @ApiModelProperty("文件名")
    private String name;
    @ApiModelProperty("文件路径")
    private String path;
    @ApiModelProperty("扩展名")
    private String extension;
    @ApiModelProperty("修改时间")
    private String updateTime;
    @ApiModelProperty("文件大小")
    private Long size;
    @ApiModelProperty("是否是目录")
    private Boolean isFolder;
    @ApiModelProperty("实际路径")
    private String url;
    @ApiModelProperty("分享码")
    private String urlKey;
}
