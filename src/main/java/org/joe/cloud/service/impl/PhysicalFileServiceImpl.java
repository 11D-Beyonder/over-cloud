package org.joe.cloud.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.joe.cloud.mapper.PhysicalFileMapper;
import org.joe.cloud.model.entity.PhysicalFile;
import org.joe.cloud.service.PhysicalFileService;
import org.springframework.stereotype.Service;

/**
 * @author Tianze Zhu
 * @since 2022-05-15
 */
@Service
public class PhysicalFileServiceImpl extends ServiceImpl<PhysicalFileMapper, PhysicalFile> implements PhysicalFileService {
}
