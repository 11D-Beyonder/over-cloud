package org.joe.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.joe.cloud.component.transfer.download.Downloader;
import org.joe.cloud.constant.FileConstant;
import org.joe.cloud.exception.FileTypeNotFoundException;
import org.joe.cloud.mapper.DeletedFileMapper;
import org.joe.cloud.mapper.PhysicalFileMapper;
import org.joe.cloud.mapper.ShareFileMapper;
import org.joe.cloud.mapper.UserFileMapper;
import org.joe.cloud.model.dto.TreeNodeDto;
import org.joe.cloud.model.dto.UserFileDto;
import org.joe.cloud.model.entity.DeletedFile;
import org.joe.cloud.model.entity.PhysicalFile;
import org.joe.cloud.model.entity.ShareFile;
import org.joe.cloud.model.entity.UserFile;
import org.joe.cloud.service.UserFileService;
import org.joe.cloud.util.DateTimeUtil;
import org.joe.cloud.util.TransferUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Tianze Zhu
 * @since 2022-05-06
 */
@Service
public class UserFileServiceImpl extends ServiceImpl<UserFileMapper, UserFile> implements UserFileService {
    @Resource
    private UserFileMapper userFileMapper;
    @Resource
    private DeletedFileMapper deletedFileMapper;
    @Resource
    private PhysicalFileMapper physicalFileMapper;
    @Resource
    private ShareFileMapper shareFileMapper;
    @Override
    public List<UserFileDto> getUserFileByPath(String path, Long currentPage, Long pageSize) {
        Long beginLoc = (currentPage - 1) * pageSize;
        return userFileMapper.selectUserFileListByPath(path, beginLoc, pageSize);
    }

    @Override
    public Long countUserFileByPath(String path) {
        return userFileMapper.selectCount(
                new LambdaQueryWrapper<UserFile>()
                        .eq(UserFile::getPath, path)
                        .eq(UserFile::getDeleted, false)
        );
    }

    @Override
    public List<UserFileDto> getUserFileByType(int type, Long currentPage, Long pageSize) {
        Long beginLoc = (currentPage - 1) * pageSize;
        if (type == FileConstant.OTHER_TYPE) {
            return userFileMapper.selectUserFileListNotInExtensions(getOtherExtensions(), beginLoc, pageSize);
        } else if (type == FileConstant.SHARE_TYPE) {
            return userFileMapper.selectUserFileListShare(beginLoc, pageSize);
        } else {
            return userFileMapper.selectUserFileListByExtension(getExtensions(type), beginLoc, pageSize);
        }
    }

    @Override
    public Long countUserFileByType(int type) {
        if (type == FileConstant.OTHER_TYPE) {
            return userFileMapper.selectCount(new LambdaQueryWrapper<UserFile>().notIn(UserFile::getExtension, getOtherExtensions()).eq(UserFile::getDeleted, false));
        } else if (type == FileConstant.SHARE_TYPE) {
            return shareFileMapper.count();
        } else {
            return userFileMapper.selectCount(
                    new LambdaQueryWrapper<UserFile>()
                            .in(UserFile::getExtension, getExtensions(type))
                            .eq(UserFile::getDeleted, false)
            );
        }
    }

    /**
     * ?????????????????????
     *
     * @return ??????????????????
     */
    @Override
    public List<UserFile> getAllFolders() {
        return userFileMapper.selectList(
                new LambdaQueryWrapper<UserFile>()
                        .eq(UserFile::getIsFolder, true)
                        .eq(UserFile::getDeleted, false)
        );
    }

    /**
     * ???????????????
     * <p>
     * ????????????????????????????????????
     *
     * @return ??????????????????
     */
    @Override
    public TreeNodeDto getFileTree() {
        List<UserFile> folderList = userFileMapper.selectList(
                new LambdaQueryWrapper<UserFile>()
                        .eq(UserFile::getIsFolder, true)
                        .eq(UserFile::getDeleted, false)
        );
        TreeNodeDto root = new TreeNodeDto();
        root.setLabel("/");
        root.setFilePath("/");
        root.setDepth(1L);
        for (UserFile folder : folderList) {
            String filePath = folder.getPath() + folder.getName() + "/";
            insertRoute(root, filePath.split("/"));
        }
        return root;
    }

    /**
     * ????????????
     *
     * @param id ????????????ID
     */
    @Override
    public void deleteUserFile(Long id) {

        UserFile userFile = userFileMapper.selectById(id);
        userFile.setDeleted(true);
        userFileMapper.updateById(userFile);
        DeletedFile deletedFile = new DeletedFile();
        deletedFile.setUserFileId(userFile.getId());
        deletedFile.setDeleteTime(DateTimeUtil.getCurrentTime());
        deletedFileMapper.insert(deletedFile);

        if (userFile.getIsFolder()) {
            /*
                ??????
                ????????????????????? /test/
                ????????????      test
                ????????????????????? /test/test/... ??????????????????????????????
             */
            List<UserFile> userFilesNeedToDelete = userFileMapper.selectList(
                    new LambdaQueryWrapper<UserFile>().likeRight(
                            UserFile::getPath, userFile.getPath() + userFile.getName() + "/"
                    )
            );
            for (UserFile f : userFilesNeedToDelete) {
                f.setDeleted(true);
                userFileMapper.updateById(f);
            }
        }
    }

    /**
     * ?????????
     *
     * @param id           ????????????ID
     * @param newName      ?????????
     * @param newExtension ?????????
     */
    @Override
    public void renameUserFile(Long id, String newName, String newExtension) {
        UserFile userFile = userFileMapper.selectById(id);
        String oldName = userFile.getName();
        String path = userFile.getPath();
        Boolean isFolder = userFile.getIsFolder();
        userFileMapper.update(null, new LambdaUpdateWrapper<UserFile>()
                .set(UserFile::getName, newName)
                .set(UserFile::getExtension, newExtension)
                .set(UserFile::getUpdateTime, DateTimeUtil.getCurrentTime())
                .eq(UserFile::getId, id));
        if (isFolder) {
            userFileMapper.updatePathByPath(path + oldName + "/", path + newName + "/");
        }
    }

    @Override
    public void moveUserFile(Long id, String newPath) {
        UserFile userFile = userFileMapper.selectById(id);
        String oldPath = userFile.getPath();
        userFile.setPath(newPath);
        userFileMapper.updateById(userFile);
        if (userFile.getIsFolder()) {
            userFileMapper.updatePathByPath(oldPath + userFile.getName() + "/", newPath + userFile.getName() + "/");
        }
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param name      ?????????
     * @param extension ?????????
     * @param path      ??????
     */
    @Override
    public boolean fileExist(String name, String extension, String path, Boolean isFolder) {
        return userFileMapper.exists(new LambdaQueryWrapper<UserFile>()
                .eq(UserFile::getPath, path)
                .eq(StringUtils.isNotEmpty(extension), UserFile::getExtension, extension)
                .eq(UserFile::getName, name)
                .eq(UserFile::getIsFolder, isFolder)
                .eq(UserFile::getDeleted, false));
    }

    /**
     * ????????????
     *
     * @param id user_file_id
     */
    @Override
    public void recoverUserFile(Long id) {
        UserFile userFile = userFileMapper.selectById(id);
        userFile.setDeleted(false);
        userFileMapper.updateById(userFile);
        QueryWrapper<DeletedFile> wrapper = new QueryWrapper<>();
        wrapper.eq("user_file_id", id);
        int deletedFile = deletedFileMapper.delete(wrapper);
        if (userFile.getIsFolder()) {
            /*
                ??????
                ????????????????????? /test/
                ????????????      test
                ????????????????????? /test/test/... ??????????????????????????????
             */
            List<UserFile> userFilesNeedToDelete = userFileMapper.selectList(
                    new LambdaQueryWrapper<UserFile>().likeRight(
                            UserFile::getPath, userFile.getPath() + userFile.getName() + "/"
                    )
            );
            for (UserFile f : userFilesNeedToDelete) {
                f.setDeleted(false);
                userFileMapper.updateById(f);
            }
        }
    }

    @Override
    public void deleterUserFileDeep(Long id) {
        UserFile userFile = userFileMapper.selectById(id);
        userFileMapper.deleteById(userFile);
        QueryWrapper<DeletedFile> wrapper = new QueryWrapper<>();
        wrapper.eq("user_file_id", id);
        int deletedFile = deletedFileMapper.delete(wrapper);
        if (userFile.getIsFolder()) {
            /*
                ??????
                ????????????????????? /test/
                ????????????      test
                ????????????????????? /test/test/... ??????????????????????????????
             */
            List<UserFile> userFilesNeedToDelete = userFileMapper.selectList(
                    new LambdaQueryWrapper<UserFile>().likeRight(
                            UserFile::getPath, userFile.getPath() + userFile.getName() + "/"
                    )
            );
            for (UserFile f : userFilesNeedToDelete) {
                userFileMapper.deleteById(f);
                //?????????????????????????????????
                //???????????????????????????????????????<=1??????????????????????????????
                PhysicalFile physicalFile = physicalFileMapper.selectById(f.getPhysicalFileId());
                if (physicalFile.getQuotationCount() <= 1) {
                    File file = new File(TransferUtil.getStaticPath() + physicalFile.getUrl());
                    if (file.exists()) {
                        if (file.delete()) {
                            physicalFileMapper.deleteById(physicalFile);
                            System.out.println(file.getName() + " ???????????????????????????");
                        } else {
                            System.out.println("???????????????????????????");
                        }
                    }
                } else {
                    physicalFile.setQuotationCount(physicalFile.getQuotationCount() - 1);
                    physicalFileMapper.updateById(physicalFile);
                }
            }
        } else {
            //???????????????????????????????????????<=1??????????????????????????????
            PhysicalFile physicalFile = physicalFileMapper.selectById(userFile.getPhysicalFileId());
            if (physicalFile.getQuotationCount() <= 1) {
                File file = new File(TransferUtil.getStaticPath() + physicalFile.getUrl());
                if (file.exists()) {
                    if (file.delete()) {
                        physicalFileMapper.deleteById(physicalFile);
                        System.out.println(file.getName() + " ???????????????????????????");
                    } else {
                        System.out.println("???????????????????????????");
                    }
                }
            } else {
                physicalFile.setQuotationCount(physicalFile.getQuotationCount() - 1);
                physicalFileMapper.updateById(physicalFile);
            }
        }
    }


    private List<String> getOtherExtensions() {
        List<String> extensions = new ArrayList<>();
        extensions.addAll(Arrays.asList(FileConstant.DOC_FILE));
        extensions.addAll(Arrays.asList(FileConstant.IMG_FILE));
        extensions.addAll(Arrays.asList(FileConstant.VIDEO_FILE));
        extensions.addAll(Arrays.asList(FileConstant.MUSIC_FILE));
        return extensions;
    }

    private List<String> getExtensions(int type) {
        if (type == FileConstant.IMAGE_TYPE) {
            return Arrays.asList(FileConstant.IMG_FILE);
        } else if (type == FileConstant.DOC_TYPE) {
            return Arrays.asList(FileConstant.DOC_FILE);
        } else if (type == FileConstant.VIDEO_TYPE) {
            return Arrays.asList(FileConstant.VIDEO_FILE);
        } else if (type == FileConstant.MUSIC_TYPE) {
            return Arrays.asList(FileConstant.MUSIC_FILE);
        } else {
            throw new FileTypeNotFoundException("???????????????????????????");
        }
    }

    /**
     * <p>
     * ??????????????? a ????????? /test/ ???
     * <p>
     * ????????? b ????????? /test/a/ ???
     * <p>
     * ????????????????????? / ??????????????????????????????
     *
     * @param root        ??????????????????
     * @param folderNames ???????????????????????????
     */
    private void insertRoute(TreeNodeDto root, String[] folderNames) {
        TreeNodeDto currentNode = root;
        for (String folderName : folderNames) {
            if (StringUtils.isEmpty(folderName)) {
                continue;
            }
            TreeNodeDto nextNode = null;
            for (TreeNodeDto child : currentNode.getChildren()) {
                // ???????????????????????????
                if (folderName.equals(child.getLabel())) {
                    nextNode = child;
                    break;
                }
            }
            if (nextNode == null) {
                // ????????????????????????????????????
                nextNode = new TreeNodeDto();
                nextNode.setLabel(folderName);
                nextNode.setFilePath(currentNode.getFilePath() + folderName + "/");
                nextNode.setDepth(currentNode.getDepth() + 1);
                currentNode.getChildren().add(nextNode);
            }
            currentNode = nextNode;
        }
    }
}
