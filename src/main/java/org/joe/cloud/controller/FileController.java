package org.joe.cloud.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.joe.cloud.common.RestResponse;
import org.joe.cloud.model.dto.DeletedFileDto;
import org.joe.cloud.model.dto.TreeNodeDto;
import org.joe.cloud.model.dto.UserFileDto;
import org.joe.cloud.model.entity.UserFile;
import org.joe.cloud.model.vo.*;
import org.joe.cloud.service.DeletedFileService;
import org.joe.cloud.service.UserFileService;
import org.joe.cloud.util.DateTimeUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tianze Zhu
 * @since 2022-05-06
 */
@ApiSupport(author = "Tianze Zhu")
@Api(tags = "文件操作")
@RestController
@RequestMapping("/file")
public class FileController {
    @Resource
    UserFileService userFileService;
    @Resource
    DeletedFileService deletedFileService;

    @ApiOperation(value = "新建文件夹", notes = "目录创建。")
    @PostMapping("/folder")
    public RestResponse createFolder(@RequestBody CreateFolderVo createFolderVo) {
        if (userFileService.count(new LambdaQueryWrapper<UserFile>()
                .eq(UserFile::getIsFolder, true)
                .eq(UserFile::getDeleted, false)
                .eq(UserFile::getName, createFolderVo.getName())
                .eq(UserFile::getPath, createFolderVo.getPath())) > 0) {
            return RestResponse.failure("同目录下文件名重复");
        }
        UserFile userFile = new UserFile();
        userFile.setName(createFolderVo.getName());
        userFile.setPath(createFolderVo.getPath());
        userFile.setIsFolder(true);
        userFile.setUpdateTime(DateTimeUtil.getCurrentTime());
        userFile.setDeleted(false);
        userFileService.save(userFile);
        return RestResponse.success("文件夹【" + userFile.getName() + "】已创建");
    }

    @ApiOperation(value = "按路径获取文件列表", notes = "用来做前台文件列表展示，data域传回一个map，“total” 为总数，“list” 为文件列表。")
    @GetMapping(value = "/file-list/path")
    public RestResponse<UserFileDto> userFileListByPath(UserFileListVo userFileListVo) {
        List<UserFileDto> userFiles = userFileService.getUserFileByPath(userFileListVo.getPath(), userFileListVo.getCurrentPage(), userFileListVo.getPageSize());
        long total = userFileService.countUserFileByPath(userFileListVo.getPath());
        Map<String, Object> res = new HashMap<>(2);
        res.put("total", total);
        res.put("list", userFiles);
        return RestResponse.success(null, res);
    }

    @ApiOperation(value = "按文件类型获取文件列表", notes = "用来做前台文件列表展示，data域传回一个map，“total” 为总数，“list” 为文件列表。")
    @GetMapping(value = "/file-list/type")
    private RestResponse<UserFileDto> userFileListByType(UserFileListVo userFileListVo) {
        List<UserFileDto> userFiles = userFileService.getUserFileByType(userFileListVo.getType(), userFileListVo.getCurrentPage(), userFileListVo.getPageSize());
        Long total = userFileService.countUserFileByType(userFileListVo.getType());
        Map<String, Object> res = new HashMap<>(2);
        res.put("total", total);
        res.put("list", userFiles);
        return RestResponse.success(null, res);
    }

    @ApiOperation(value = "删除文件", notes = "可以删除文件或者目录。")
    @PutMapping("/delete-flag")
    public RestResponse deleteFile(@RequestBody DeleteFileVo deleteFileVo) {
        userFileService.deleteUserFile(deleteFileVo.getId());
        return RestResponse.success();
    }

    @ApiOperation(value = "获取文件树", notes = "文件移动的时候需要用到该接口，用来展示目录树。")
    @GetMapping("/tree")
    public RestResponse<TreeNodeDto> getFileTree() {
        return RestResponse.success(null, userFileService.getFileTree());
    }

    @ApiOperation(value = "重命名", notes = "重命名文件或目录，可修改后缀。")
    @PutMapping("/name-extension")
    public RestResponse renameFile(@RequestBody RenameFileVo renameFileVo) {
        UserFile userFile = userFileService.getById(renameFileVo.getId());
        if (userFileService.fileExist(renameFileVo.getName(), renameFileVo.getExtension(), userFile.getPath(), renameFileVo.getIsFolder())) {
            return RestResponse.failure("同名文件已存在！");
        }
        userFileService.renameUserFile(renameFileVo.getId(), renameFileVo.getName(), renameFileVo.getExtension());
        return RestResponse.success();
    }

    @ApiOperation(value = "移动文件", notes = "不能移动到子级目录")
    @PutMapping("/path")
    public RestResponse moveFile(@RequestBody MoveFileVo moveFileVo) {
        UserFile userFile = userFileService.getById(moveFileVo.getId());
        if (userFile.getIsFolder() && moveFileVo.getNewPath().startsWith(userFile.getPath() + userFile.getName() + "/")) {
            return RestResponse.failure("不可移动到子级目录");
        }
        if (userFileService.fileExist(userFile.getName(), userFile.getExtension(), moveFileVo.getNewPath(), userFile.getIsFolder())) {
            return RestResponse.failure("同名文件已存在！");
        }
        userFileService.moveUserFile(moveFileVo.getId(), moveFileVo.getNewPath());
        return RestResponse.success();
    }

    @ApiOperation(value = "批量移动文件", notes = "不能移动到子级目录")
    @PutMapping("/batch-path")
    public RestResponse batchMoveFile(@RequestBody BatchMoveFileVo batchMoveFileVo) {
        List<UserFileDto> userFileDtos = JSON.parseArray(batchMoveFileVo.getFiles(), UserFileDto.class);
        for (UserFileDto userFileDto : userFileDtos) {
            if (userFileService.fileExist(userFileDto.getName(), userFileDto.getExtension(), batchMoveFileVo.getNewPath(), userFileDto.getIsFolder())) {
                return RestResponse.failure("同名文件已存在");
            }
            if (userFileDto.getIsFolder() && batchMoveFileVo.getNewPath().startsWith(userFileDto.getPath() + userFileDto.getName() + "/")) {
                return RestResponse.failure("不可移动到子级目录");
            }
        }
        for (UserFileDto userFileDto : userFileDtos) {
            userFileService.moveUserFile(userFileDto.getId(), batchMoveFileVo.getNewPath());
        }
        return RestResponse.success();
    }

    @ApiOperation(value = "批量删除", notes = "逻辑删除")
    @PutMapping("/batch-delete-flag")
    public RestResponse batchDeleteFile(@RequestBody BatchDeleteFileVo batchDeleteFileVo) {
        List<UserFileDto> userFileDtos = JSON.parseArray(batchDeleteFileVo.getFiles(), UserFileDto.class);
        for (UserFileDto userFileDto : userFileDtos) {
            userFileService.deleteUserFile(userFileDto.getId());
        }
        return RestResponse.success();
    }

    @ApiOperation(value = "回收站", notes = "查看已删除文件")
    @GetMapping("/trash")
    public RestResponse<DeletedFileDto> getDeletedFile(DeletedFileListVo deletedFileListVo) {
        Map<String, Object> res = new HashMap<>(2);
        res.put("total", deletedFileService.count());
        res.put("list", deletedFileService.getAllDeletedFile(deletedFileListVo.getCurrentPage(), deletedFileListVo.getPageSize()));
        return RestResponse.success(null, res);
    }

    @ApiOperation(value = "恢复文件", notes = "可以恢复回收站中的文件或者目录")
    @PutMapping("/recover-flag")
    public RestResponse recoverFile(@RequestBody DeleteFileVo deleteFileVo) {
        userFileService.recoverUserFile(deleteFileVo.getId());
        return RestResponse.success();
    }
    @ApiOperation(value = "彻底删除文件", notes = "可以彻底删除回收站中的文件或者目录")
    @PutMapping("/delete-deep")
    public RestResponse deleteFileDeep(@RequestBody DeleteFileVo deleteFileVo) {
        userFileService.deleterUserFileDeep(deleteFileVo.getId());
        return RestResponse.success();
    }
}
