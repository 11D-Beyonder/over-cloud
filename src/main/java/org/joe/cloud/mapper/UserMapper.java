package org.joe.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.joe.cloud.model.entity.User;

/**
 * @author Tianze Zhu
 * @since 2022-05-02
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
