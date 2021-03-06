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
        //??????????????????????????????????????????
        UserFile userFile = userFileMapper.selectById(uploadFileVo.getId());
        if (userFile != null) {
            //????????????????????????
            Uploader uploader = transferToolFactory.getUploader();
            UploadFileDto uploadFileDto = uploader.upload(httpServletRequest, uploadFileVo);
            if (uploadFileDto.getSuccess()) {
                //????????????????????????????????????????????????
                PhysicalFile physicalFile = physicalFileMapper.selectById(userFile.getPhysicalFileId());
                if (physicalFile.getQuotationCount() > 1) {
                    //?????????????????????,?????????????????????????????????????????????????????????????????????,????????????????????????physicalId
                    physicalFile.setQuotationCount(physicalFile.getQuotationCount()-1);
                    physicalFileMapper.updateById(physicalFile);

                    //??????id?????????????????????????????????????????????id
                    physicalFile.setId(null);
                    physicalFile.setQuotationCount(1l);
                    physicalFile.setIdentifier(uploadFileVo.getIdentifier());
                    physicalFile.setSize(uploadFileVo.getTotalSize());
                    physicalFile.setUrl(uploadFileDto.getUrl());
                    physicalFileMapper.insertFile(physicalFile);//?????????id???????????????

                    userFile.setName(uploadFileDto.getFileName());
                    userFile.setExtension(uploadFileDto.getFileExtension());
                    userFile.setUpdateTime(DateTimeUtil.getCurrentTime());
                    userFile.setPhysicalFileId(physicalFile.getId());
                    userFileMapper.updateById(userFile);



                } else {
                    //?????????????????????????????????????????????????????????????????????
                    String FilePath = TransferUtil.getStaticPath() + physicalFile.getUrl();
                    String minFilePath = TransferUtil.getStaticPath() + physicalFile.getUrl() + "_min";

                    File targetFile = new File(FilePath);
                    targetFile.delete();
                    //?????????????????????????????????
                    File minFile = new File(minFilePath);
                    if (minFile.exists())
                        minFile.delete();

                    //???????????????????????????
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
        //???????????????????????????????????????
        UserFile userFile = userFileMapper.selectById(uploadSmallFileVo.getId());
        if (userFile != null) {
            Uploader uploader = transferToolFactory.getUploader();
            //??????????????????
            UploadFileDto uploadFileDto = uploader.uploadSmall(httpServletRequest, uploadSmallFileVo);

            if (uploadFileDto.getSuccess()) {
                //????????????????????????????????????????????????
                PhysicalFile physicalFile = physicalFileMapper.selectById(userFile.getPhysicalFileId());
                if (physicalFile.getQuotationCount() > 1) {
                    //???????????????????????????????????????????????????????????????????????????????????????

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
                    //?????????????????????????????????
                    File minFile = new File(minFilePath);
                    if (minFile.exists())
                        minFile.delete();


                    //???????????????????????????
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

        //????????????????????????????????????pom.xml??????????????????????????????????????????????????????????????????
        File file = new File(TransferUtil.getStaticPath() + physicalFile.getUrl());

        //??????????????????
        long startByte = 0;
        //??????????????????
        long endByte = file.length() - 1;

        //???range??????
        if (range != null && range.contains("bytes=") && range.contains("-")) {
            range = range.substring(range.lastIndexOf("=") + 1).trim();
            String ranges[] = range.split("-");
            try {
                //??????range?????????????????????????????????
                if (ranges.length == 1) {
                    //??????1?????????bytes=-1024  ?????????????????????1024??????????????????
                    if (range.startsWith("-")) {
                        endByte = Long.parseLong(ranges[0]);
                    }
                    //??????2?????????bytes=1024-  ???1024?????????????????????????????????
                    else if (range.endsWith("-")) {
                        startByte = Long.parseLong(ranges[0]);
                    }
                }
                //??????3?????????bytes=1024-2048  ???1024????????????2048??????????????????
                else if (ranges.length == 2) {
                    startByte = Long.parseLong(ranges[0]);
                    endByte = Long.parseLong(ranges[1]);
                }

            } catch (NumberFormatException e) {
                startByte = 0;
                endByte = file.length() - 1;
            }
        }

        //??????????????????
        long contentLength = endByte - startByte + 1;

        //???????????????
        //https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Accept-Ranges
        httpServletResponse.setHeader("Accept-Ranges", "bytes");
        //Content-Type ???????????????????????????????????????
//        httpServletResponse.setHeader("Content-Type", contentType);
        //Content-Disposition ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        // ??????????????????????????????????????????????????????inline???????????????????????????????????????????????????
        httpServletResponse.setHeader("Content-Disposition", "inline;filename=pom.xml");
        //Content-Length ?????????????????????????????????????????????
        httpServletResponse.setHeader("Content-Length", String.valueOf(contentLength));
        //Content-Range ??????????????????????????????????????????[????????????????????????]-[????????????]/[???????????????]
        httpServletResponse.setHeader("Content-Range", "bytes " + startByte + "-" + endByte + "/" + file.length());

        httpServletResponse.setStatus(httpServletResponse.SC_OK);
//        httpServletResponse.setContentType(contentType);

        BufferedOutputStream outputStream = null;
        RandomAccessFile randomAccessFile = null;
        //?????????????????????
        long transmitted = 0;
        try {
            randomAccessFile = new RandomAccessFile(file, "r");
            outputStream = new BufferedOutputStream(httpServletResponse.getOutputStream());
            byte[] buff = new byte[2048];
            int len = 0;
            randomAccessFile.seek(startByte);
            //??????????????????????????????2048???buff???length??????byte
            while ((transmitted + len) <= contentLength && (len = randomAccessFile.read(buff)) != -1) {
                outputStream.write(buff, 0, len);
                transmitted += len;
            }
            //????????????buff.length??????
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
