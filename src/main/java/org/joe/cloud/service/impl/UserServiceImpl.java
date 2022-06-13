package org.joe.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.joe.cloud.mapper.UserMapper;
import org.joe.cloud.model.entity.User;
import org.joe.cloud.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Tianze Zhu
 * @since 2022-05-02
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    UserMapper userMapper;

    @Override
    public User loadUserByUsername(String username) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUsername, username);
        return userMapper.selectOne(userLambdaQueryWrapper);
    }
}
