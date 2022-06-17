package org.joe.cloud.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.joe.cloud.common.RestResponse;
import org.joe.cloud.mapper.UserFileMapper;
import org.joe.cloud.model.entity.PhysicalFile;
import org.joe.cloud.model.entity.UserFile;
import org.joe.cloud.model.vo.DownloadFileVo;
import org.joe.cloud.model.vo.UploadFileVo;
import org.joe.cloud.model.vo.UploadSmallFileVo;
import org.joe.cloud.service.PhysicalFileService;
import org.joe.cloud.service.TransferService;
import org.joe.cloud.service.UserFileService;
import org.joe.cloud.util.DateTimeUtil;
import org.joe.cloud.util.FileUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tianze Zhu
 * @since 2022-05-15
 */
@ApiSupport(author = "Tianze Zhu")
@Api(tags = "文件传输")
@RestController
@Slf4j
@RequestMapping("/transfer")
public class TransferController {
    @Resource
    private PhysicalFileService physicalFileService;
    @Resource
    private UserFileService userFileService;
    @Resource
    private TransferService transferService;
    @Resource
    private UserFileMapper userFileMapper;

    @ApiOperation(value = "检查是否已上传", notes = "校验文件MD5判断文件是否存在，如果存在直接上传成功并返回data.skip=true，如果不存在返回skip=false需要再次调用该接口的POST方法")
    @GetMapping(value = "/upload")
    public RestResponse upload(UploadFileVo uploadFileVo) {
        Map<String, Boolean> res = new HashMap<>(1);
        System.out.println(uploadFileVo.getIdentifier());
        synchronized (TransferController.class) {
            PhysicalFile physicalFile = physicalFileService.getOne(new LambdaQueryWrapper<PhysicalFile>().eq(PhysicalFile::getIdentifier, uploadFileVo.getIdentifier()));
            if (physicalFile != null) {
                // 不必重复上传，指向同一个资源即可。
                physicalFile.setQuotationCount(physicalFile.getQuotationCount() + 1);
                physicalFileService.updateById(physicalFile);

                UserFile userFile = new UserFile();
                userFile.setPhysicalFileId(physicalFile.getId());
                userFile.setUpdateTime(DateTimeUtil.getCurrentTime());
                userFile.setPath(uploadFileVo.getFilePath());
                userFile.setDeleted(false);
                userFile.setName(FileUtil.getFileName(uploadFileVo.getFilename()));
                userFile.setExtension(FileUtil.getFileExtension(uploadFileVo.getFilename()));
                userFile.setIsFolder(false);
                userFileService.save(userFile);
                res.put("skip", true);
            } else {
                res.put("skip", false);
            }
        }
        return RestResponse.success(null, res);
    }

    @ApiOperation(value = "上传大文件", notes = "上传大文件接口，分片上传")
    @PostMapping("/upload")
    public RestResponse upload(HttpServletRequest httpServletRequest, UploadFileVo uploadFileVo) {
        transferService.upload(httpServletRequest, uploadFileVo);
        return RestResponse.success();
    }
    @ApiOperation(value = "更新小文件", notes = "更新小文件接口,不用分片，用于支持代码，md等小文档在线编辑，网页api")
    @PostMapping("/updateMd")
    public RestResponse updateSmall(HttpServletRequest httpServletRequest, UploadSmallFileVo uploadSmallFileVo) {
        transferService.updateSmall(httpServletRequest, uploadSmallFileVo);
        return RestResponse.success();
    }
    @ApiOperation(value = "更新大文件", notes = "更新大文件接口，分片，用于支持同步，只需用户文件id一样，文件名和内容都可变更，客户端api")
    @PostMapping("/update")
    public RestResponse update(HttpServletRequest httpServletRequest, UploadFileVo uploadFileVo) {
        transferService.update(httpServletRequest, uploadFileVo);
        return RestResponse.success();
    }

    @ApiOperation(value = "下载文件", notes = "下载文件接口，支持分段下载")
    @GetMapping("/download")
    public void download(HttpServletResponse httpServletResponse, DownloadFileVo downloadFileVo) {
        transferService.download(httpServletResponse, downloadFileVo);
    }

    @ApiOperation(value = "从分享下载文件", notes = "下载文件接口，链接即key的思想，支持分段下载")
    @GetMapping("/share/download")
    public void download(HttpServletResponse httpServletResponse, @RequestParam String url) {
        //先判断url是否存在，存在则返回用户文件对象，并走正常下载流程，否则失败
        DownloadFileVo downloadFileVo = userFileMapper.getFileDtoByUrl(url);
        if (downloadFileVo != null) {
            transferService.download(httpServletResponse, downloadFileVo);
        }
    }
}
