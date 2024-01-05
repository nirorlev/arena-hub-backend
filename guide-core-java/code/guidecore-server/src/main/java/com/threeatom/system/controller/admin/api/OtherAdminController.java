//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.system.controller.admin.api;

import com.threeatom.common.controller.BaseController;
import com.threeatom.common.controller.Message;
import com.threeatom.system.entity.SysUser;
import io.swagger.annotations.ApiOperation;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/v1/admin"})
public class OtherAdminController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OtherAdminController.class);

    public OtherAdminController() {
    }

    @ApiOperation("运行健康检查")
    @GetMapping({"/loginCheck"})
    public Message loginCheck() {
        LOGGER.info("loginCheck!!!!\ud83d\udd27\ud83d\udd27\ud83d\udd27");
        SysUser user = new SysUser();
        user.setUpdateTime(new Date());
        user.setCreateTime(new Date());
        return (new Message()).ok(200, "").addData("date", user);
    }
}
