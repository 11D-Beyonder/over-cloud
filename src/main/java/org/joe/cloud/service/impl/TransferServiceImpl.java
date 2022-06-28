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
import java.io.*;
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

                    //注意id需置为空，让数据库自行决定新的id
                    physicalFile.setId(null);
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

    @Override
    public void chunkdownload(String range, HttpServletResponse httpServletResponse, DownloadFileVo downloadFileVo) {
        UserFile userFile = userFileMapper.selectById(downloadFileVo.getId());
        String fileName = userFile.getName();
        if (userFile.getExtension() != null) {
            fileName += "." + userFile.getExtension();
        }
        fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        PhysicalFile physicalFile = physicalFileMapper.selectById(userFile.getPhysicalFileId());

        //要下载的文件，此处以项目pom.xml文件举例说明。实际项目请根据实际业务场景获取
        File file = new File(TransferUtil.getStaticPath() + physicalFile.getUrl());

        //开始下载位置
        long startByte = 0;
        //结束下载位置
        long endByte = file.length() - 1;

        //有range的话
        if (range != null && range.contains("bytes=") && range.contains("-")) {
            range = range.substring(range.lastIndexOf("=") + 1).trim();
            String ranges[] = range.split("-");
            try {
                //根据range解析下载分片的位置区间
                if (ranges.length == 1) {
                    //情况1，如：bytes=-1024  从开始字节到第1024个字节的数据
                    if (range.startsWith("-")) {
                        endByte = Long.parseLong(ranges[0]);
                    }
                    //情况2，如：bytes=1024-  第1024个字节到最后字节的数据
                    else if (range.endsWith("-")) {
                        startByte = Long.parseLong(ranges[0]);
                    }
                }
                //情况3，如：bytes=1024-2048  第1024个字节到2048个字节的数据
                else if (ranges.length == 2) {
                    startByte = Long.parseLong(ranges[0]);
                    endByte = Long.parseLong(ranges[1]);
                }

            } catch (NumberFormatException e) {
                startByte = 0;
                endByte = file.length() - 1;
            }
        }

        //要下载的长度
        long contentLength = endByte - startByte + 1;

        //响应头设置
        //https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Accept-Ranges
        httpServletResponse.setHeader("Accept-Ranges", "bytes");
        //Content-Type 表示资源类型，如：文件类型
//        httpServletResponse.setHeader("Content-Type", contentType);
        //Content-Disposition 表示响应内容以何种形式展示，是以内联的形式（即网页或者页面的一部分），还是以附件的形式下载并保存到本地。
        // 这里文件名换成下载后你想要的文件名，inline表示内联的形式，即：浏览器直接下载
        httpServletResponse.setHeader("Content-Disposition", "inline;filename=pom.xml");
        //Content-Length 表示资源内容长度，即：文件大小
        httpServletResponse.setHeader("Content-Length", String.valueOf(contentLength));
        //Content-Range 表示响应了多少数据，格式为：[要下载的开始位置]-[结束位置]/[文件总大小]
        httpServletResponse.setHeader("Content-Range", "bytes " + startByte + "-" + endByte + "/" + file.length());

        httpServletResponse.setStatus(httpServletResponse.SC_OK);
//        httpServletResponse.setContentType(contentType);

        BufferedOutputStream outputStream = null;
        RandomAccessFile randomAccessFile = null;
        //已传送数据大小
        long transmitted = 0;
        try {
            randomAccessFile = new RandomAccessFile(file, "r");
            outputStream = new BufferedOutputStream(httpServletResponse.getOutputStream());
            byte[] buff = new byte[2048];
            int len = 0;
            randomAccessFile.seek(startByte);
            //判断是否到了最后不足2048（buff的length）个byte
            while ((transmitted + len) <= contentLength && (len = randomAccessFile.read(buff)) != -1) {
                outputStream.write(buff, 0, len);
                transmitted += len;
            }
            //处理不足buff.length部分
            if (transmitted < contentLength) {
                len = randomAccessFile.read(buff, 0, (int) (contentLength - transmitted));
                outputStream.write(buff, 0, len);
                transmitted += len;
            }

            outputStream.flush();
            httpServletResponse.flushBuffer();
            randomAccessFile.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
