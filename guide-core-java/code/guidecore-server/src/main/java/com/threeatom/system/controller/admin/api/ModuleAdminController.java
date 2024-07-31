package com.threeatom.system.controller.admin.api;

import com.threeatom.common.controller.BaseController;
import com.threeatom.common.controller.Message;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/v1/admin/module"})
public class ModuleAdminController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleAdminController.class);
    private static final String PERM_PREFIX = "system:module:";

    public ModuleAdminController() {}

    @ApiOperation("模块列表")
    @GetMapping({"/list"})
    @RequiresPermissions({"system:module:add"})
    public Message list() {
        return (new Message()).ok(200, "").addData("list", (Object) null);
    }
}
