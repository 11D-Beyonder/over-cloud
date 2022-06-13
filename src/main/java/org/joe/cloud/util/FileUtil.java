package org.joe.cloud.util;

import org.joe.cloud.constant.FileConstant;

/**
 * @author Tianze Zhu
 * @since 2022-05-15
 */
public class FileUtil {
    public static boolean isImageFile(String extension) {
        for (String t : FileConstant.IMG_FILE) {
            if (extension.equalsIgnoreCase(t)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isVideoFile(String extension) {
        for (String t : FileConstant.VIDEO_FILE) {
            if (extension.equalsIgnoreCase(t)) {
                return true;
            }
        }
        return false;
    }

    public static String getFileExtension(String fileName) {
        if (fileName.lastIndexOf(".") == -1) {
            return null;
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public static String getFileName(String originalName) {
        if (originalName.lastIndexOf(".") == -1) {
            return originalName;
        }
        return originalName.substring(0, originalName.lastIndexOf("."));
    }
}
