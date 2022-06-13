package org.joe.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.joe.cloud.model.dto.DeletedFileDto;
import org.joe.cloud.model.entity.DeletedFile;

import java.util.List;

/**
 * @author Tianze Zhu
 * @since 2022-05-10
 */
@Mapper
public interface DeletedFileMapper extends BaseMapper<DeletedFile> {
    public List<DeletedFileDto> selectAllDeletedFile(Long beginLoc, Long pageSize);
}
