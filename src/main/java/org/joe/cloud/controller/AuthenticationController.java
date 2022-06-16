package org.joe.cloud.controller;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.joe.cloud.common.RestResponse;
import org.joe.cloud.component.JwtProcessor;
import org.joe.cloud.mapper.PhysicalFileMapper;
import org.joe.cloud.model.entity.User;
import org.joe.cloud.model.vo.EnterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tianze Zhu
 * @since 2022-05-03
 */
@ApiSupport(author = "Tianze Zhu")
@Api(tags = "权限认证")
@RestController
public class AuthenticationController {
    @Autowired
    PhysicalFileMapper physicalFileMapper;
    @ApiOperation(value = "登录", notes = "用户名密码登录，获得token，data.token中是token")
    @PostMapping("/login")
    public RestResponse login(@RequestBody EnterVo enterVo) {
        String username = enterVo.getUsername();
        String password = enterVo.getPassword();
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password);
        SecurityUtils.getSubject().login(usernamePasswordToken);
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Map<String, String> res = new HashMap<>(1);
        res.put("token", JwtProcessor.generateToken(user.getUsername()));
        return RestResponse.success("您已登录", res);
    }

    @ApiOperation(value = "获取用户信息", notes = "在shiro上下文中得到用户信息，data.username得到用户名。")
    @GetMapping("/transfer/storage")
    public RestResponse storage() {

        Map<String, Long> res = new HashMap<>(1);
        res.put("storage_used", physicalFileMapper.getUsedStorage());
        res.put("storage_total", 60*1024*1024*1024l);
        return RestResponse.success("空间", res);
    }
    @ApiOperation(value = "获取用户信息", notes = "在shiro上下文中得到用户信息，data.username得到用户名。")
    @GetMapping("/who")
    public RestResponse who(@RequestHeader("Authorization") String token) {
        String username = JwtProcessor.getUsernameByToken(token.substring(7));
        Map<String, String> res = new HashMap<>(1);
        res.put("username", username);
        return RestResponse.success("已登录", res);
    }
}
