package org.joe.cloud.model.dto;

import lombok.Data;

/**
 * @author Tianze Zhu
 * @since 2022-05-15
 */
@Data
public class UploadFileDto {
    private String fileName;
    private String url;
    private String fileExtension;
    private Boolean success;
}
