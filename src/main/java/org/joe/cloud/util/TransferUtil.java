package org.joe.cloud.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

/**
 * @author Tianze Zhu
 * @since 2022-05-15
 */
@Slf4j
public class TransferUtil {
    private static String FILE_SEPARATOR = "/";

    private static String urlDecode(String url) {
        String decodeUrl = null;
        try {
            decodeUrl = URLDecoder.decode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return decodeUrl;
    }

    public static String getStaticPath() {
        String projectRootAbsolutePath = getProjectRootPath();
        int index = projectRootAbsolutePath.indexOf("file:");
        if (index != -1) {
            projectRootAbsolutePath = projectRootAbsolutePath.substring(0, index);
        }
        return projectRootAbsolutePath + "static" + FILE_SEPARATOR;
    }

    public static String getProjectRootPath() {
        String absolutePath = null;
        try {
            String url = ResourceUtils.getURL("classpath:").getPath();
            absolutePath = urlDecode(new File(url).getAbsolutePath()) + FILE_SEPARATOR;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return absolutePath;
    }

    public static void merge(String targetFile, String folder, String fileName) {
        try {
            // 创建合并后的文件
            Files.createFile(Paths.get(targetFile));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        try (Stream<Path> stream = Files.list(Paths.get(folder))) {
            stream.filter(path -> !path.getFileName().toString().equals(fileName))
                    .sorted((f1, f2) -> {
                        String p1 = f1.getFileName().toString();
                        String p2 = f2.getFileName().toString();
                        int chunkNumber1 = Integer.parseInt(p1.substring(p1.lastIndexOf("-")));
                        int chunkNumber2 = Integer.parseInt(p2.substring(p2.lastIndexOf("-")));
                        return Integer.compare(chunkNumber1, chunkNumber2);
                    })
                    .forEach(path -> {
                        try {
                            // 以追加形式写入文件
                            Files.write(Paths.get(targetFile), Files.readAllBytes(path), StandardOpenOption.APPEND);
                            // 合并后删除分块
                            Files.delete(path);
                        } catch (IOException e) {
                            log.error(e.getMessage(), e);
                        }
                    });
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
