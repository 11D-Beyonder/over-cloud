package org.joe.cloud.component.transfer.upload;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.joe.cloud.model.dto.UploadFileDto;
import org.joe.cloud.model.vo.UploadFileVo;
import org.joe.cloud.util.TransferUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Tianze Zhu
 * @since 2022-05-15
 */
@Slf4j
public abstract class Uploader {
    public static final String ROOT_PATH = "upload";
    public static final String FILE_SEPARATOR = "/";

    /**
     * 由各类Uploader继承
     *
     * @param httpServletRequest 包含Part的请求
     * @param uploadFileVo       文件上传VO
     * @return 上传文件的信息
     */
    public abstract UploadFileDto upload(HttpServletRequest httpServletRequest, UploadFileVo uploadFileVo);

    protected String generateSavePath() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String path = ROOT_PATH + FILE_SEPARATOR + formatter.format(new Date());
        String staticPath = TransferUtil.getStaticPath();
        File dir = new File(staticPath + path);
        if (!dir.exists()) {
            try {
                if (!dir.mkdirs()) {
                    log.error("目录创建失败：" + TransferUtil.getStaticPath() + path);
                }
            } catch (Exception e) {
                log.error("目录创建失败" + TransferUtil.getStaticPath() + path);
                return "";
            }
        }
        return path;
    }

    /**
     * 判断文件是否上传完成
     *
     * @param uploadFileVo 分块
     * @param statusFile   状态文件
     * @return 是否上传完成
     * @throws IOException 文件IO时出现错误
     */
    public synchronized Boolean checkUploadStatus(UploadFileVo uploadFileVo, File statusFile) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(statusFile, "rw");
        // 设置文件长度
        randomAccessFile.setLength(uploadFileVo.getTotalChunks());
        // 设置起始偏移量
        randomAccessFile.seek(uploadFileVo.getChunkNumber() - 1);
        // 将指定的一个字节写入status文件中
        randomAccessFile.write(Byte.MAX_VALUE);
        byte[] completeStatusList = FileUtils.readFileToByteArray(statusFile);
        // 不关闭会造成无法占用
        randomAccessFile.close();
        // 创建文件长为总分片数
        // 每上传一个分片就向conf文件中写入127
        // 那么没上传的位置就是默认的0
        // 已上传的就是127
        for (byte b : completeStatusList) {
            if (b != Byte.MAX_VALUE) {
                return false;
            }
        }
        // 上传完成则删除记录文件
        statusFile.delete();
        return true;
    }
}
