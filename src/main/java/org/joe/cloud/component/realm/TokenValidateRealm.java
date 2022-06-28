package org.joe.cloud.component.realm;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.joe.cloud.component.JwtProcessor;
import org.joe.cloud.model.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author Tianze Zhu
 * @since 2022-05-03
 */

@Slf4j
@Component
public class TokenValidateRealm extends AuthenticatingRealm {


    public TokenValidateRealm() {
        super((token, info) -> {
            String bearerToken = ((BearerToken) token).getToken();
            return JwtProcessor.verify(bearerToken);
        });
    }

    @Override
    public Class getAuthenticationTokenClass() {
        return BearerToken.class;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String bearerToken = ((BearerToken) token).getToken();
        User user = new User();
        user.setUsername(JwtProcessor.getUsernameByToken(bearerToken));
        return new SimpleAuthenticationInfo(user, bearerToken, "TokenValidateRealm");
    }

}
