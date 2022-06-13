package org.joe.cloud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.joe.cloud.model.dto.DeletedFileDto;
import org.joe.cloud.model.entity.DeletedFile;

import java.util.List;

/**
 * @author Tianze Zhu
 * @since 2022-05-10
 */
public interface DeletedFileService extends IService<DeletedFile> {
    List<DeletedFileDto> getAllDeletedFile(Long currentPage, Long pageSize);
}
