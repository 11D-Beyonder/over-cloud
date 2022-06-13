package org.joe.cloud.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.joe.cloud.mapper.DeletedFileMapper;
import org.joe.cloud.model.dto.DeletedFileDto;
import org.joe.cloud.model.entity.DeletedFile;
import org.joe.cloud.service.DeletedFileService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Tianze Zhu
 * @since 2022-05-10
 */
@Service
public class DeletedFileServiceImpl extends ServiceImpl<DeletedFileMapper, DeletedFile> implements DeletedFileService {
    @Resource
    DeletedFileMapper deletedFileMapper;

    @Override
    public List<DeletedFileDto> getAllDeletedFile(Long currentPage, Long pageSize) {
        return deletedFileMapper.selectAllDeletedFile((currentPage - 1) * pageSize, pageSize);
    }
}
