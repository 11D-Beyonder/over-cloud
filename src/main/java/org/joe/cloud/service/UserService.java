package org.joe.cloud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.joe.cloud.model.entity.User;

/**
 * @author Tianze Zhu
 * @since 2022-05-02
 */
public interface UserService extends IService<User> {
    User loadUserByUsername(String username);
}
