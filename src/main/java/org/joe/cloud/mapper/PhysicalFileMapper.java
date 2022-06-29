package org.joe.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.joe.cloud.model.entity.PhysicalFile;

/**
 * @author Tianze Zhu
 * @since 2022-05-15
 */
@Mapper
public interface PhysicalFileMapper extends BaseMapper<PhysicalFile> {
    Long getUsedStorage();
    Integer insertFile(PhysicalFile physicalFile);
}
