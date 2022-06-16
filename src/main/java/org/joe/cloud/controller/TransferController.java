package org.joe.cloud.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.joe.cloud.common.RestResponse;
import org.joe.cloud.model.entity.PhysicalFile;
import org.joe.cloud.model.entity.UserFile;
import org.joe.cloud.model.vo.DownloadFileVo;
import org.joe.cloud.model.vo.UploadFileVo;
import org.joe.cloud.service.PhysicalFileService;
import org.joe.cloud.service.TransferService;
import org.joe.cloud.service.UserFileService;
import org.joe.cloud.util.DateTimeUtil;
import org.joe.cloud.util.FileUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @ApiOperation(value = "上传文件", notes = "上传文件接口")
    @PostMapping("/upload")
    public RestResponse upload(HttpServletRequest httpServletRequest, UploadFileVo uploadFileVo) {
        transferService.upload(httpServletRequest, uploadFileVo);
        return RestResponse.success();
    }

    @ApiOperation(value = "下载文件", notes = "下载文件接口")
    @GetMapping("/download")
    public void download(HttpServletResponse httpServletResponse, DownloadFileVo downloadFileVo) {
        transferService.download(httpServletResponse, downloadFileVo);
    }
}
