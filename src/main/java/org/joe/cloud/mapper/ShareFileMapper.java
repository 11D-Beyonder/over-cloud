package org.joe.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.joe.cloud.model.entity.ShareFile;
import org.joe.cloud.model.entity.User;

/**
 * @author Tianze Zhu
 * @since 2022-05-02
 */
@Mapper
public interface ShareFileMapper extends BaseMapper<ShareFile> {
    Long count();
    Integer insertAndUpdate(Integer id,String url);

}
