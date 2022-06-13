package org.joe.cloud.service.impl;

import org.joe.cloud.common.StorageLocationEnum;
import org.joe.cloud.component.transfer.download.Downloader;
import org.joe.cloud.component.transfer.factory.TransferToolFactory;
import org.joe.cloud.component.transfer.upload.Uploader;
import org.joe.cloud.mapper.PhysicalFileMapper;
import org.joe.cloud.mapper.UserFileMapper;
import org.joe.cloud.model.dto.UploadFileDto;
import org.joe.cloud.model.entity.PhysicalFile;
import org.joe.cloud.model.entity.UserFile;
import org.joe.cloud.model.vo.DownloadFileVo;
import org.joe.cloud.model.vo.UploadFileVo;
import org.joe.cloud.service.TransferService;
import org.joe.cloud.util.DateTimeUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * @author Tianze Zhu
 * @since 2022-05-15
 */
@Service
public class TransferServiceImpl implements TransferService {
    @Resource
    private TransferToolFactory transferToolFactory;

    @Resource
    private PhysicalFileMapper physicalFileMapper;
    @Resource
    private UserFileMapper userFileMapper;

    @Override
    public void upload(HttpServletRequest httpServletRequest, UploadFileVo uploadFileVo) {
        Uploader uploader = transferToolFactory.getUploader();
        UploadFileDto uploadFileDto = uploader.upload(httpServletRequest, uploadFileVo);
        if (uploadFileDto.getSuccess()) {
            PhysicalFile physicalFile = new PhysicalFile();
            physicalFile.setIdentifier(uploadFileVo.getIdentifier());
            physicalFile.setSize(uploadFileVo.getTotalSize());
            physicalFile.setQuotationCount(1L);
            physicalFile.setStorageLocation(StorageLocationEnum.LOCAL);
            physicalFile.setUrl(uploadFileDto.getUrl());
            physicalFileMapper.insert(physicalFile);

            UserFile userFile = new UserFile();
            userFile.setName(uploadFileDto.getFileName());
            userFile.setExtension(uploadFileDto.getFileExtension());
            userFile.setDeleted(false);
            userFile.setPath(uploadFileVo.getFilePath());
            userFile.setIsFolder(false);
            userFile.setPhysicalFileId(physicalFile.getId());
            userFile.setUpdateTime(DateTimeUtil.getCurrentTime());
            userFileMapper.insert(userFile);
        }
    }

    @Override
    public void download(HttpServletResponse httpServletResponse, DownloadFileVo downloadFileVo) {
        UserFile userFile = userFileMapper.selectById(downloadFileVo.getId());
        String fileName = userFile.getName();
        if (userFile.getExtension() != null) {
            fileName += "." + userFile.getExtension();
        }
        fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        httpServletResponse.setContentType("application/force-download");
        httpServletResponse.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
        PhysicalFile physicalFile = physicalFileMapper.selectById(userFile.getPhysicalFileId());
        Downloader downloader = transferToolFactory.getDownloader();
        downloader.download(httpServletResponse, physicalFile.getUrl());
    }
}
