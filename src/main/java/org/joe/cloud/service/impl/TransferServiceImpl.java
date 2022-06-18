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
import org.joe.cloud.model.vo.UploadSmallFileVo;
import org.joe.cloud.service.TransferService;
import org.joe.cloud.util.DateTimeUtil;
import org.joe.cloud.util.TransferUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
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
    public void update(HttpServletRequest httpServletRequest, UploadFileVo uploadFileVo) {
        //先判断要更新的大文件是否存在
        UserFile userFile = userFileMapper.selectById(uploadFileVo.getId());
        if (userFile != null) {
            //走正常大文件上传
            Uploader uploader = transferToolFactory.getUploader();
            UploadFileDto uploadFileDto = uploader.upload(httpServletRequest, uploadFileVo);
            if (uploadFileDto.getSuccess()) {
                //先根据查到的信息删除旧的实际文件
                PhysicalFile physicalFile = physicalFileMapper.selectById(userFile.getPhysicalFileId());
                if (physicalFile.getQuotationCount() > 1) {
                    //不能删除旧文件，新增一条物理文件记录，同时修改用户文件信息

                    userFile.setName(uploadFileDto.getFileName());
                    userFile.setExtension(uploadFileDto.getFileExtension());
                    userFile.setUpdateTime(DateTimeUtil.getCurrentTime());
                    userFileMapper.updateById(userFile);

                    physicalFile.setQuotationCount(1l);
                    physicalFile.setIdentifier(uploadFileVo.getIdentifier());
                    physicalFile.setSize(uploadFileVo.getTotalSize());
                    physicalFile.setUrl(uploadFileDto.getUrl());
                    physicalFileMapper.insert(physicalFile);

                } else {
                    //删除旧文件，修改物理文件记录，修改用户文件信息
                    String FilePath = TransferUtil.getStaticPath() + physicalFile.getUrl();
                    String minFilePath = TransferUtil.getStaticPath() + physicalFile.getUrl() + "_min";

                    File targetFile = new File(FilePath);
                    targetFile.delete();
                    //不是所有文件都有缩略图
                    File minFile = new File(minFilePath);
                    if (minFile.exists())
                        minFile.delete();

                    //再更新逻辑文件信息
                    userFile.setName(uploadFileDto.getFileName());
                    userFile.setExtension(uploadFileDto.getFileExtension());
                    userFile.setUpdateTime(DateTimeUtil.getCurrentTime());
                    userFileMapper.updateById(userFile);

                    physicalFile.setIdentifier(uploadFileVo.getIdentifier());
                    physicalFile.setSize(uploadFileVo.getTotalSize());
                    physicalFile.setUrl(uploadFileDto.getUrl());
                    physicalFileMapper.updateById(physicalFile);
                }


            }
        }
    }

    @Override
    public void updateSmall(HttpServletRequest httpServletRequest, UploadSmallFileVo uploadSmallFileVo) {
        //先判断要更新的文件是否存在
        UserFile userFile = userFileMapper.selectById(uploadSmallFileVo.getId());
        if (userFile != null) {
            Uploader uploader = transferToolFactory.getUploader();
            //走小文件上传
            UploadFileDto uploadFileDto = uploader.uploadSmall(httpServletRequest, uploadSmallFileVo);

            if (uploadFileDto.getSuccess()) {
                //先根据查到的信息删除旧的实际文件
                PhysicalFile physicalFile = physicalFileMapper.selectById(userFile.getPhysicalFileId());
                if (physicalFile.getQuotationCount() > 1) {
                    //不能删除旧文件，新增一条物理文件记录，同时修改用户文件信息

                    userFile.setName(uploadFileDto.getFileName());
                    userFile.setExtension(uploadFileDto.getFileExtension());
                    userFile.setUpdateTime(DateTimeUtil.getCurrentTime());
                    userFileMapper.updateById(userFile);

                    physicalFile.setQuotationCount(1l);
                    physicalFile.setIdentifier(uploadSmallFileVo.getIdentifier());
                    physicalFile.setSize(uploadSmallFileVo.getTotalSize());
                    physicalFile.setUrl(uploadFileDto.getUrl());
                    physicalFileMapper.insert(physicalFile);

                } else {
                    String FilePath = TransferUtil.getStaticPath() + physicalFile.getUrl();
                    String minFilePath = TransferUtil.getStaticPath() + physicalFile.getUrl() + "_min";

                    File targetFile = new File(FilePath);
                    targetFile.delete();
                    //不是所有文件都有缩略图
                    File minFile = new File(minFilePath);
                    if (minFile.exists())
                        minFile.delete();


                    //再更新逻辑文件信息
                    userFile.setName(uploadFileDto.getFileName());
                    userFile.setExtension(uploadFileDto.getFileExtension());
                    userFile.setIsFolder(false);
                    userFile.setUpdateTime(DateTimeUtil.getCurrentTime());
                    userFileMapper.updateById(userFile);

                    physicalFile.setIdentifier(uploadSmallFileVo.getIdentifier());
                    physicalFile.setSize(uploadSmallFileVo.getTotalSize());
                    physicalFile.setStorageLocation(StorageLocationEnum.LOCAL);
                    physicalFile.setUrl(uploadFileDto.getUrl());
                    physicalFileMapper.updateById(physicalFile);
                }
            }
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
