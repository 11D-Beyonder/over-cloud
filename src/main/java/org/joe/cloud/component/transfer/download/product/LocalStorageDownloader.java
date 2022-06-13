package org.joe.cloud.component.transfer.download.product;

import org.joe.cloud.component.transfer.download.Downloader;
import org.joe.cloud.util.TransferUtil;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @author Tianze Zhu
 * @since 2022-06-11
 */
@Component
public class LocalStorageDownloader extends Downloader {
    @Override
    public void download(HttpServletResponse httpServletResponse, String fileUrl) {
        byte[] buffer = new byte[1024];
        File file = new File(TransferUtil.getStaticPath() + fileUrl);
        BufferedInputStream bufferedInputStream = null;
        if (file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                bufferedInputStream = new BufferedInputStream(fileInputStream);
                OutputStream outputStream = httpServletResponse.getOutputStream();
                for (int i = bufferedInputStream.read(buffer); i != -1; i = bufferedInputStream.read(buffer)) {
                    outputStream.write(buffer, 0, i);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bufferedInputStream != null) {
                    try {
                        bufferedInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
