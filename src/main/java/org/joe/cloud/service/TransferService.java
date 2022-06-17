package org.joe.cloud.service;

import org.joe.cloud.model.vo.DownloadFileVo;
import org.joe.cloud.model.vo.UploadFileVo;
import org.joe.cloud.model.vo.UploadSmallFileVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Tianze Zhu
 * @since 2022-05-15
 */
public interface TransferService {
    void upload(HttpServletRequest httpServletRequest, UploadFileVo uploadFileVo);
    void update(HttpServletRequest httpServletRequest, UploadFileVo uploadFileVo);
    void updateSmall(HttpServletRequest httpServletRequest, UploadSmallFileVo uploadSmallFileVo);
    void download(HttpServletResponse httpServletResponse, DownloadFileVo downloadFileVo);
}
