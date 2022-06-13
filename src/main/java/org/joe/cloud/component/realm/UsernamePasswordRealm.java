package org.joe.cloud.component.realm;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.joe.cloud.model.entity.User;
import org.joe.cloud.service.UserService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Tianze Zhu
 * @since 2022-05-03
 */
@Slf4j
@Component
public class UsernamePasswordRealm extends AuthenticatingRealm {
    @Resource
    UserService userService;

    /**
     * 构造器设置Matcher
     */
    public UsernamePasswordRealm() {
        super();
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("SHA-256");
        this.setCredentialsMatcher(hashedCredentialsMatcher);
    }

    /**
     * Realm处理 用户名-密码 登录验证
     */
    @Override
    public Class getAuthenticationTokenClass() {
        return UsernamePasswordToken.class;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        User user = userService.loadUserByUsername(usernamePasswordToken.getUsername());
        return new SimpleAuthenticationInfo(user, user.getPassword(), "UsernamePasswordRealm");
    }
}
