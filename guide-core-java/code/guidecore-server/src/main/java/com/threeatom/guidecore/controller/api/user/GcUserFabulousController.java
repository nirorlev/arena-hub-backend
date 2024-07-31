package com.threeatom.guidecore.controller.api.user;

import com.threeatom.common.controller.Message;
import com.threeatom.guidecore.controller.GuideCoreController;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.GcUserFabulous;
import com.threeatom.guidecore.service.GcUserFabulousService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "generator")
@RestController
@RequestMapping("/api/v1/guidecore/user/fabulous")
public class GcUserFabulousController extends GuideCoreController {

    @Autowired private GcUserFabulousService gcUserFabulousService;

    @ApiOperation(value = "点赞评论", httpMethod = "POST")
    @PostMapping("/saveFabulous")
    public Message saveFabulous(@RequestBody GcUserFabulous gcUserFabulous) {
        GcUser user = this.getGcUser();
        gcUserFabulous.setUserId(user.getId());
        GcUserFabulous fabulous = gcUserFabulousService.getUserFabulous(gcUserFabulous);
        if (fabulous != null) {
            gcUserFabulousService.removeById(fabulous.getId());
        } else {
            if (null != gcUserFabulous.getEventId()) {
                gcUserFabulous.setCommentId(gcUserFabulous.getTargetUserId());
            }
            gcUserFabulousService.saveOrUpdate(gcUserFabulous);
        }
        return new Message().ok();
    }
}
