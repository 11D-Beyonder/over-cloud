package org.joe.cloud.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.joe.cloud.common.RestResponse;
import org.joe.cloud.mapper.ShareFileMapper;
import org.joe.cloud.model.dto.DeletedFileDto;
import org.joe.cloud.model.dto.TreeNodeDto;
import org.joe.cloud.model.dto.UserFileDto;
import org.joe.cloud.model.entity.ShareFile;
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

@ApiSupport(author = "Qian Qiu")
@Api(tags = "文件分享操作")
@RestController
@RequestMapping("/share")
@CrossOrigin
public class FileShareController {
    @Resource
    UserFileService userFileService;
    @Resource
    DeletedFileService deletedFileService;
    @Resource
    ShareFileMapper shareFileMapper;

    @ApiOperation(value = "添加分享文件")
    @PutMapping(value = "/add")
    private RestResponse<UserFileDto> userFileListByType(@RequestBody ShareFile shareFile) {
        if (shareFileMapper.insert(shareFile) > 0)
            return RestResponse.success(null);
        else return RestResponse.failure();

    }

    @ApiOperation(value = "删除文件", notes = "可以删除文件或者目录。")
    @PutMapping("/delete-flag")
    public RestResponse deleteFile(@RequestBody DeleteFileVo deleteFileVo) {
        userFileService.deleteUserFile(deleteFileVo.getId());
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

}
