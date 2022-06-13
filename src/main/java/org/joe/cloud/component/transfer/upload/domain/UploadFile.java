package org.joe.cloud.component.transfer.upload.domain;

/**
 * @author Tianze Zhu
 * @since 2022-05-15
 */
public class UploadFile {
    private Integer chunkNumber;
    private Long chunkSize;
    private Integer totalChunks;
    private String identifier;
    private Long totalSize;
    private Long currentChunkSize;
}
