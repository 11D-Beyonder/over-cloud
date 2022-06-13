package org.joe.cloud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.joe.cloud.model.dto.TreeNodeDto;
import org.joe.cloud.model.dto.UserFileDto;
import org.joe.cloud.model.entity.UserFile;

import java.util.List;

/**
 * @author Tianze Zhu
 * @since 2022-05-03
 */
public interface UserFileService extends IService<UserFile> {
    List<UserFileDto> getUserFileByPath(String path, Long currentPage, Long pageSize);

    Long countUserFileByPath(String path);

    List<UserFileDto> getUserFileByType(int type, Long currentPage, Long pageSize);

    Long countUserFileByType(int type);

    /**
     * 获得所有文件夹
     *
     * @return 用户文件列表
     */
    List<UserFile> getAllFolders();

    /**
     * 获取文件树
     * <p>
     * 数据库中有所有文件的路径
     *
     * @return 文件树根节点
     */
    TreeNodeDto getFileTree();

    /**
     * 逻辑删除
     *
     * @param id 用户文件ID
     */
    void deleteUserFile(Long id);

    /**
     * 重命名
     *
     * @param id           用户文件ID
     * @param newName      新名字
     * @param newExtension 新后缀
     */
    void renameUserFile(Long id, String newName, String newExtension);

    /**
     * 移动
     *
     * @param id      用户文件id
     * @param newPath 新路径
     */
    void moveUserFile(Long id, String newPath);

    /**
     * 查看指定目录下是否存在同名文件
     *
     * @param name      文件名
     * @param extension 后缀名
     * @param path      路径
     * @param isFolder  是否为文件夹
     */
    boolean fileExist(String name, String extension, String path, Boolean isFolder);
}
