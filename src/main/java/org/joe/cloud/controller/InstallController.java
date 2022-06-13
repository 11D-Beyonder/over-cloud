package org.joe.cloud.controller;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.joe.cloud.common.RestResponse;
import org.joe.cloud.model.entity.User;
import org.joe.cloud.model.vo.EnterVo;
import org.joe.cloud.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Tianze Zhu
 * @since 2022-05-03
 */
@ApiSupport(author = "Tianze Zhu")
@Api(tags = "安装")
@RestController
public class InstallController {
    @Resource
    UserService userService;

    @ApiOperation(value = "安装", notes = "初始化用户名密码")
    @PostMapping("/install")
    public RestResponse install(@RequestBody EnterVo enterVo) {
        if (userService.count(null) > 0) {
            return RestResponse.failure("不得重复安装");
        }
        String username = enterVo.getUsername();
        String password = enterVo.getPassword();
        User user = new User();
        user.setUsername(username);
        password = new SimpleHash("SHA-256", password).toString();
        user.setPassword(password);
        userService.save(user);
        return RestResponse.success("安装成功");
    }
}
