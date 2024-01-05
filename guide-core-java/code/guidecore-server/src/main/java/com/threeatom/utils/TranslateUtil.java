package com.threeatom.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.threeatom.utils.data.TransData;
import com.threeatom.utils.data.TransResult;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;


/**
 * @author Administrator
 * @title: TranslateUtil
 * @projectName guidecore
 * @description: TODO
 * @date 2021/11/11/01115:34
 */
public class TranslateUtil {
    /**
     * 百度翻译接口地址
     */


    /**
     * 获得翻译结果
     * @param query
     * @param from
     * @param to
     * @return
     * @throws IOException
     */
    public static List<TransData> getTransResult(String query, String from, String to, String TRANS_API_HOST, String appid, String securityKey) throws IOException {

        Map<String, String> params = buildParams(query, from, to,appid,securityKey);
        JSONObject jsonObject;
        //当请求翻译内容过长 用post
        if (query.length() >= 2000) {
            //post 请求方式
            jsonObject = HttpUtil.doPostStr(TRANS_API_HOST, params);
        } else {
            //  get请求方式
            String url = getUrlWithQueryString(TRANS_API_HOST, params);
            jsonObject = HttpUtil.doGetStr(url);
        }
        if (jsonObject.get("error_code")!=null) {
            System.out.println("失败原因"+jsonObject);
            return new ArrayList<TransData>();
        }else{
            TransResult transResult = JSON.parseObject(jsonObject.toString(), TransResult.class);
            return transResult.getTrans_result();
        }
    }

    /**
     * 构建参数map
     *
     * @param query
     * @param from
     * @param to
     * @return
     * @throws UnsupportedEncodingException
     */
    private static Map<String, String> buildParams(String query, String from, String to,String appid,String securityKey) throws UnsupportedEncodingException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);

        params.put("appid", appid);

        // 随机数
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("salt", salt);

        // 签名
        String src = appid + query + salt + securityKey; // 加密前的原文
        params.put("sign", MD5.md5(src));

        return params;
    }


    /**
     * 拼接url get方式拼接参数  返回url
     *
     * @param url
     * @param params
     * @return
     */
    public static String getUrlWithQueryString(String url, Map<String, String> params) {
        if (params == null) {
            return url;
        }

        StringBuilder builder = new StringBuilder(url);
        if (url.contains("?")) {
            builder.append("&");
        } else {
            builder.append("?");
        }

        int i = 0;
        for (String key : params.keySet()) {
            String value = params.get(key);
            if (value == null) { // 过滤空的key
                continue;
            }

            if (i != 0) {
                builder.append('&');
            }

            builder.append(key);
            builder.append('=');
            builder.append(encode(value));

            i++;
        }

        return builder.toString();
    }


    /**
     * 对输入的字符串进行URL编码, 即转换为%20这种形式
     *
     * @param input 原文
     * @return URL编码. 如果编码失败, 则返回原文
     */
    public static String encode(String input) {
        if (input == null) {
            return "";
        }

        try {
            return URLEncoder.encode(input, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return input;
    }

    public static Map<String, String> getParamsFromRequest(HttpServletRequest request) {
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            params.put(name, valueStr);
        }
        return params;
    }

}
