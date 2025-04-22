package com.threeatom.system.controller.admin.api;

import com.alibaba.fastjson.JSONObject;
import com.threeatom.common.ApiAssert;
import com.threeatom.common.controller.BaseController;
import com.threeatom.common.controller.Message;
import com.threeatom.common.exception.SystemException;
import com.threeatom.system.service.SysUserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/v1/admin/user"})
public class UserAdminController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserAdminController.class);
    @Autowired private SysUserService userService;

    public UserAdminController() {}

    @ApiOperation(value = "后端登陆", notes = "不需要token,常规登录使用", httpMethod = "POST")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "username", value = "用户名，手机号", required = true, dataType = "String"),
        @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String"),
        @ApiImplicitParam(name = "sysId", value = "密码", required = true, dataType = "Integer")
    })
    @PostMapping({"/login"})
    public Message login(@RequestBody JSONObject jsonParams) throws SystemException {
        String username = jsonParams.getString("username");
        String password = jsonParams.getString("password");
        Integer sysId = jsonParams.getInteger("sysId");
        ApiAssert.notNull(sysId, "sysid不能为null");
        ApiAssert.notEmpty(username, "username不能为空");
        ApiAssert.notEmpty(password, "password不能为空");
        String token = this.userService.getTokenByLoginUser(sysId, username, password);
        return StringUtils.isEmpty(token)
                ? (new Message()).error(401, "该实例没有相关账号，请注册")
                : (new Message()).ok(200, "").addData("token", token);
    }
}
