package com.threeatom.system.plugin;

import com.alibaba.fastjson.JSONObject;
import com.threeatom.common.controller.Message;
import com.threeatom.common.exception.SystemException;

public interface UserHelper extends SystemBaseHelper {
    Message apiWeixinLogin(JSONObject jsonObject) throws SystemException;
}
