package org.joe.cloud.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Tianze Zhu
 * @since 2022-05-15
 */
@Data
@ApiModel("分片上传VO")
public class UploadFileVo {
    @ApiModelProperty(value = "文件id，仅更新上传时使用", example = "1", notes = "从1开始标号")
    private Integer id;
    @ApiModelProperty(value = "分片数", example = "1", notes = "从1开始标号")
    private Integer chunkNumber;
    @ApiModelProperty(value = "分片大小")
    private Long chunkSize;
    @ApiModelProperty("总分片数")
    private Integer totalChunks;
    @ApiModelProperty("文件md5")
    private String identifier;
    @ApiModelProperty("文件大小")
    private Long totalSize;
    @ApiModelProperty(value = "当前分片大小", notes = "最后一个分片可能会小于chunkSize，其它都等于。")
    private Long currentChunkSize;
    @ApiModelProperty(value = "文件名")
    private String filename;
    @ApiModelProperty(value = "保存路径，更新时不用传，如果需要更新路径，请使用移动文件接口")
    private String filePath;
}
