package com.threeatom.system.controller.admin.api;

import com.threeatom.common.controller.BaseController;
import com.threeatom.common.controller.Message;
import com.threeatom.system.entity.SysRole;
import com.threeatom.system.entity.SysUser;
import com.threeatom.system.service.SysRoleService;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/v1/admin/role"})
public class RoleAdminController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleAdminController.class);
    private static final String PERM_PREFIX = "system:role:";
    @Autowired private SysRoleService roleService;

    public RoleAdminController() {}

    @ApiOperation("权限列表")
    @GetMapping({"/list"})
    @RequiresPermissions({"system:role:list"})
    public Message list() {
        SysUser user = this.getSysUser();
        List<SysRole> list = this.roleService.getRoles(user.getSysId());
        return (new Message()).ok(200, "").addData("list", list);
    }
}
