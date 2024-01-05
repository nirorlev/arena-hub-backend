//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.common.exception;

import com.alibaba.fastjson.JSON;
import com.threeatom.common.controller.Message;
import java.io.PrintWriter;
import javax.servlet.ServletResponse;
import org.apache.shiro.web.util.WebUtils;

public class ShiroGlobalExceptionUtil {
    public ShiroGlobalExceptionUtil() {
    }

    public static void exceptionHandler(Exception e, ServletResponse response) {
        Message msg = (new Message()).error(401, e.getMessage());
        responseWrite(JSON.toJSONString(msg), response);
    }

    public static void responseWrite(String outStr, ServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter printWriter = null;

        try {
            printWriter = WebUtils.toHttp(response).getWriter();
            printWriter.write(outStr);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }
}
