package org.joe.cloud.constant;

/**
 * 我们将常用的文件格式按照后缀分为4类，
 * 分别是图像类、文档类、视频类、音乐类，
 * 除了这4类的格式，其他格式我们统一放到其他类中，
 * 这样当做查询的时侯，查询具体某一类的文件，
 * 只需要传入对应的文件格式数组即可，查询其他类，则需要排除这些类。
 *
 * @author Joe
 * @since 2022-04-22
 */
public class FileConstant {
    public static final String[] IMG_FILE = {"bmp", "jpg", "png", "tif", "gif", "jpeg"};
    public static final String[] DOC_FILE = {"doc", "docx", "ppt", "pptx", "xls", "xlsx", "txt", "hlp", "wps", "rtf", "html", "pdf", "md"};
    public static final String[] VIDEO_FILE = {"mp4", "mov", "m4a"};
    public static final String[] MUSIC_FILE = {"wav", "aif", "au", "mp3", "ram", "wma", "mmf", "amr", "aac", "flac"};
    public static final int IMAGE_TYPE = 1;
    public static final int DOC_TYPE = 2;
    public static final int VIDEO_TYPE = 3;
    public static final int MUSIC_TYPE = 4;
    public static final int OTHER_TYPE = 5;
}
