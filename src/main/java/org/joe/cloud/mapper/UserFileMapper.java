package org.joe.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.joe.cloud.model.dto.UserFileDto;
import org.joe.cloud.model.entity.UserFile;

import java.util.List;

/**
 * @author Tianze Zhu
 * @since 2022-05-03
 */
@Mapper
public interface UserFileMapper extends BaseMapper<UserFile> {
    /**
     * 分页获取特定目录下的文件列表
     * left join 物理文件表
     *
     * @param path     文件目录
     * @param beginLoc 数据库起始位置
     * @param pageSize 页面大小
     * @return 文件列表
     */
    List<UserFileDto> selectUserFileListByPath(String path, Long beginLoc, Long pageSize);

    List<UserFileDto> selectUserFileListByExtension(List<String> extensions, Long beginLoc, Long pageSize);

    List<UserFileDto> selectUserFileListNotInExtensions(List<String> extensions, Long beginLoc, Long pageSize);

    void updatePathByPath(String oldPath, String newPath);
}
