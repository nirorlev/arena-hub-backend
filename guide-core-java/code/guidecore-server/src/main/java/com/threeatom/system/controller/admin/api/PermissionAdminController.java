//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.system.controller.admin.api;

import com.threeatom.common.controller.BaseController;
import com.threeatom.common.controller.Message;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/v1/admin/permission"})
public class PermissionAdminController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleAdminController.class);
    private static final String PERM_PREFIX = "system:permission:";

    public PermissionAdminController() {
    }

    @ApiOperation("权限列表")
    @GetMapping({"/list"})
    @RequiresPermissions({"system:permission:list"})
    public Message list() {
        return (new Message()).ok(200, "").addData("list", (Object)null);
    }

    @ApiOperation("刷新当前用户权限")
    @PostMapping({"/flush"})
    public Message flush() {
        this.clearAuthorizationCache();
        return (new Message()).ok(200, "");
    }
}
