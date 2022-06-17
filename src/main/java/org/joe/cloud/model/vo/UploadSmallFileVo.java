package org.joe.cloud.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Tianze Zhu
 * @since 2022-05-15
 */
@Data
@ApiModel("小文件更新VO，不用分片")
public class UploadSmallFileVo {
    @ApiModelProperty(value = "文件id，仅更新上传时使用")
    private Integer id;
    @ApiModelProperty("文件md5")
    private String identifier;
    @ApiModelProperty("文件大小")
    private Long totalSize;
    @ApiModelProperty(value = "文件名")
    private String filename;
}
