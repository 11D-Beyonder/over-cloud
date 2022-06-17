package org.joe.cloud.component.transfer.upload.product;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.joe.cloud.component.transfer.upload.Uploader;
import org.joe.cloud.exception.NotSameFileExpection;
import org.joe.cloud.exception.UploadException;
import org.joe.cloud.model.dto.UploadFileDto;
import org.joe.cloud.model.vo.UploadFileVo;
import org.joe.cloud.model.vo.UploadSmallFileVo;
import org.joe.cloud.util.FileUtil;
import org.joe.cloud.util.TransferUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Objects;

/**
 * @author Tianze Zhu
 * @since 2022-05-15
 */
@Component
public class LocalStorageUploader extends Uploader {
    @Override
    public synchronized UploadFileDto upload(HttpServletRequest httpServletRequest, UploadFileVo uploadFileVo) {

        StandardMultipartHttpServletRequest standardMultipartHttpServletRequest = (StandardMultipartHttpServletRequest) httpServletRequest;
        if (!ServletFileUpload.isMultipartContent(standardMultipartHttpServletRequest)) {
            throw new UploadException("未包含文件上传域");
        }

        String savePath = generateSavePath();
        MultipartFile multipartFile = standardMultipartHttpServletRequest.getFile("file");
        assert multipartFile != null;
        String fileExtension = FileUtil.getFileExtension(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        String saveFilePath = savePath + FILE_SEPARATOR + uploadFileVo.getIdentifier();
        if (fileExtension != null) {
            saveFilePath += "." + fileExtension;
        }
        String tempFilePath = savePath + FILE_SEPARATOR + uploadFileVo.getIdentifier() + "_tmp";
        String statusFilePath = savePath + FILE_SEPARATOR + uploadFileVo.getIdentifier() + "." + "status";
        String minFilePath = savePath + FILE_SEPARATOR + uploadFileVo.getIdentifier() + "_min";
        if (fileExtension != null) {
            minFilePath += ".png";
        }
        File targetFile = new File(TransferUtil.getStaticPath() + FILE_SEPARATOR + saveFilePath);
        File tempFile = new File(TransferUtil.getStaticPath() + FILE_SEPARATOR + tempFilePath);
        File minFile = new File(TransferUtil.getStaticPath() + FILE_SEPARATOR + minFilePath);
        File statusFile = new File(TransferUtil.getStaticPath() + FILE_SEPARATOR + statusFilePath);

        try {
            // 1. 打开将要写的文件
            RandomAccessFile randomAccessFile = new RandomAccessFile(tempFile, "rw");
            // 2. 打开通道
            FileChannel fileChannel = randomAccessFile.getChannel();
            // 3. 计算偏移量
            long position = (uploadFileVo.getChunkNumber() - 1) * uploadFileVo.getChunkSize();
            // 4. 获取分片数据
            byte[] fileData = multipartFile.getBytes();
            // 5. 写入数据
            fileChannel.position(position);
            // 将文件数据写入缓冲区再写入文件
            fileChannel.write(ByteBuffer.wrap(fileData));
            // 更新文件内容和元数据
            fileChannel.force(true);
            fileChannel.close();
            randomAccessFile.close();

            UploadFileDto uploadFileDto = new UploadFileDto();
            uploadFileDto.setFileName(FileUtil.getFileName(multipartFile.getOriginalFilename()));
            uploadFileDto.setFileExtension(FileUtil.getFileExtension(Objects.requireNonNull(multipartFile.getOriginalFilename())));
            uploadFileDto.setUrl(saveFilePath);

            if (checkUploadStatus(uploadFileVo, statusFile)) {
                FileInputStream fileInputStream = new FileInputStream(tempFile.getPath());
                String md5 = DigestUtils.md5DigestAsHex(fileInputStream);
                fileInputStream.close();
                if (StringUtils.isNotBlank(md5) && !md5.equals(uploadFileVo.getIdentifier())) {
                    throw new NotSameFileExpection();
                }
                tempFile.renameTo(targetFile);
                if (FileUtil.isImageFile(uploadFileDto.getFileExtension())) {
                    try {
                        Thumbnails.of(targetFile).size(300, 300).toFile(minFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (FileUtil.isVideoFile(uploadFileDto.getFileExtension())) {
                    FFmpegFrameGrabber frameGrabber = FFmpegFrameGrabber.createDefault(targetFile.getPath());
                    frameGrabber.start();
                    for (int i = 0; i < frameGrabber.getLengthInFrames(); i++) {
                        Frame frame = frameGrabber.grabFrame();
                        if (frame != null && frame.image != null) {
                            BufferedImage bufferedImage = new Java2DFrameConverter().getBufferedImage(frame);
                            try {
                                ImageIO.write(bufferedImage, "png", minFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }
                    frameGrabber.stop();
                }
                uploadFileDto.setSuccess(true);
            } else {
                uploadFileDto.setSuccess(false);
            }
            return uploadFileDto;
        } catch (NotSameFileExpection e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new UploadException("未包含文件上传域");
        }
        return null;
    }

    @Override
    public synchronized UploadFileDto uploadSmall(HttpServletRequest httpServletRequest, UploadSmallFileVo uploadSmallFileVo) {
        StandardMultipartHttpServletRequest standardMultipartHttpServletRequest = (StandardMultipartHttpServletRequest) httpServletRequest;
        if (!ServletFileUpload.isMultipartContent(standardMultipartHttpServletRequest)) {
            throw new UploadException("未包含文件上传域");
        }

        String savePath = generateSavePath();
        MultipartFile multipartFile = standardMultipartHttpServletRequest.getFile("file");
        assert multipartFile != null;
        String fileExtension = FileUtil.getFileExtension(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        String saveFilePath = savePath + FILE_SEPARATOR + uploadSmallFileVo.getIdentifier();
        if (fileExtension != null) {
            saveFilePath += "." + fileExtension;
        }
        String tempFilePath = savePath + FILE_SEPARATOR + uploadSmallFileVo.getIdentifier() + "_tmp";
        String minFilePath = savePath + FILE_SEPARATOR + uploadSmallFileVo.getIdentifier() + "_min";
        if (fileExtension != null) {
            minFilePath += ".png";
        }
        File targetFile = new File(TransferUtil.getStaticPath() + FILE_SEPARATOR + saveFilePath);
        File tempFile = new File(TransferUtil.getStaticPath() + FILE_SEPARATOR + tempFilePath);
        File minFile = new File(TransferUtil.getStaticPath() + FILE_SEPARATOR + minFilePath);


        try {
            // 1. 打开将要写的文件
            RandomAccessFile randomAccessFile = new RandomAccessFile(tempFile, "rw");
            // 2. 打开通道
            FileChannel fileChannel = randomAccessFile.getChannel();
            // 3. 计算偏移量，小文件不走分片，默认为0
            long position = 0;
            // 4. 获取分片数据
            byte[] fileData = multipartFile.getBytes();
            // 5. 写入数据
            fileChannel.position(position);
            // 将文件数据写入缓冲区再写入文件
            fileChannel.write(ByteBuffer.wrap(fileData));
            // 更新文件内容和元数据
            fileChannel.force(true);
            fileChannel.close();
            randomAccessFile.close();

            UploadFileDto uploadFileDto = new UploadFileDto();
            uploadFileDto.setFileName(FileUtil.getFileName(multipartFile.getOriginalFilename()));
            uploadFileDto.setFileExtension(FileUtil.getFileExtension(Objects.requireNonNull(multipartFile.getOriginalFilename())));
            uploadFileDto.setUrl(saveFilePath);

            //判断md5是否相同
            FileInputStream fileInputStream = new FileInputStream(tempFile.getPath());
            String md5 = DigestUtils.md5DigestAsHex(fileInputStream);
            fileInputStream.close();
            if (StringUtils.isNotBlank(md5) && !md5.equals(uploadSmallFileVo.getIdentifier())) {
                throw new NotSameFileExpection();
            }
            tempFile.renameTo(targetFile);
            //生成缩略图
            if (FileUtil.isImageFile(uploadFileDto.getFileExtension())) {
                try {
                    Thumbnails.of(targetFile).size(300, 300).toFile(minFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (FileUtil.isVideoFile(uploadFileDto.getFileExtension())) {
                FFmpegFrameGrabber frameGrabber = FFmpegFrameGrabber.createDefault(targetFile.getPath());
                frameGrabber.start();
                for (int i = 0; i < frameGrabber.getLengthInFrames(); i++) {
                    Frame frame = frameGrabber.grabFrame();
                    if (frame != null && frame.image != null) {
                        BufferedImage bufferedImage = new Java2DFrameConverter().getBufferedImage(frame);
                        try {
                            ImageIO.write(bufferedImage, "png", minFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
                frameGrabber.stop();
            }
            uploadFileDto.setSuccess(true);
            return uploadFileDto;
        } catch (NotSameFileExpection e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new UploadException("未包含文件上传域");
        }
        return null;
    }
}
