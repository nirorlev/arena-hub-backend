package com.threeatom.utils;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @author Administrator
 * @title: HttpUtil
 * @projectName guidecore
 * @description: TODO
 * @date 2021/11/5/00511:17
 */
public class HttpUtil {
    /**
     * 发送post请求，根据 Content-Type 返回不同的返回值
     *
     * @param url
     * @param header
     * @param body
     * @return
     */
    public static Map<String, Object> doPost2(String url, Map<String, String> header, String body) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        PrintWriter out = null;
        try {
            // 设置 url
            URL realUrl = new URL(url);
            URLConnection connection = realUrl.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
            // 设置 header
            for (String key : header.keySet()) {
                httpURLConnection.setRequestProperty(key, header.get(key));
            }
            // 设置请求 body
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            out = new PrintWriter(httpURLConnection.getOutputStream());
            // 保存body
            out.print(body);
            // 发送body
            out.flush();
            if (HttpURLConnection.HTTP_OK != httpURLConnection.getResponseCode()) {
                BufferedReader br =
                        new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
                System.out.println(
                        "Http 请求失败，状态码：" + httpURLConnection.getResponseCode() + "，错误信息：" + br.readLine());
                return null;
            }
            // 获取响应header
            String responseContentType = httpURLConnection.getHeaderField("Content-Type");
            if ("audio/mpeg".equals(responseContentType)) {
                // 获取响应body
                byte[] bytes = toByteArray(httpURLConnection.getInputStream());
                resultMap.put("Content-Type", "audio/mpeg");
                resultMap.put("sid", httpURLConnection.getHeaderField("sid"));
                resultMap.put("body", bytes);
                return resultMap;
            } else {
                // 设置请求 body
                BufferedReader in =
                        new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String line;
                String result = "";
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                resultMap.put("Content-Type", "text/plain");
                resultMap.put("body", result);

                return resultMap;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 发送post请求
     *
     * @param url
     * @param header
     * @param body
     * @return
     */
    public static String doPost1(String url, Map<String, String> header, String body) {
        String result = "";
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            // 设置 url
            URL realUrl = new URL(url);
            URLConnection connection = realUrl.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
            // 设置 header
            for (String key : header.keySet()) {
                httpURLConnection.setRequestProperty(key, header.get(key));
            }
            // 设置请求 body
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            out = new PrintWriter(httpURLConnection.getOutputStream());
            // 保存body
            out.print(body);
            // 发送body
            out.flush();
            if (HttpURLConnection.HTTP_OK != httpURLConnection.getResponseCode()) {
                return null;
            }

            // 获取响应body
            in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            return null;
        }
        return result;
    }

    public static String sendPostFormUrlencoded(String url, Map<String, String> body) {
        // 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> forms = new LinkedMultiValueMap<String, String>();
        for (String keys : body.keySet()) {
            forms.put(keys, Collections.singletonList(body.get(keys)));
        }
        org.springframework.http.HttpEntity<MultiValueMap<String, String>> httpEntity =
                new org.springframework.http.HttpEntity<MultiValueMap<String, String>>(forms, headers);
        // 获取返回数据
        String result = restTemplate.postForObject(url, httpEntity, String.class);
        return result;
    }

    /**
     * 流转二进制数组
     *
     * @param in
     * @return
     * @throws IOException
     */
    private static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }

    /**
     *  get 请求
     * @param url
     * @return
     * @throws IOException
     */
    public static JSONObject doGetStr(String url) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            String content = EntityUtils.toString(entity, "UTF-8");
            return JSONObject.parseObject(content);
        }
        return null;
    }

    public static JSONObject doGetAuthorization(String url, String token) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        org.springframework.http.HttpEntity<String> requestEntity =
                new org.springframework.http.HttpEntity<>(null, headers);
        ResponseEntity<String> resEntity =
                restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        return JSONObject.parseObject(resEntity.getBody());
    }

    /**
     * post 请求 String装填
     * @param url
     * @param reqContent
     * @return
     * @throws IOException
     */
    public static JSONObject doPostStr(String url, String reqContent) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        if (!StringUtils.isEmpty(reqContent)) {
            httpPost.setEntity(new StringEntity(reqContent, "UTF-8"));
        }
        CloseableHttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            String resContent = EntityUtils.toString(entity, "UTF-8");
            return JSONObject.parseObject(resContent);
        }
        return null;
    }

    /**
     * post 请求 map装填
     * @param url
     * @param reqContent
     * @return
     * @throws IOException
     */
    public static JSONObject doPostStr(String url, Map<String, String> reqContent)
            throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        // 装填参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        if (reqContent != null) {
            for (Map.Entry<String, String> entry : reqContent.entrySet()) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        // 设置参数到请求对象中
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));

        CloseableHttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            String resContent = EntityUtils.toString(entity, "UTF-8");
            return JSONObject.parseObject(resContent);
        }
        return null;
    }

    public static JSONObject post(String url, String body, ContentType contentType, Header[] headers) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        StringEntity requestEntity = new StringEntity(body, contentType);
        httpPost.setEntity(requestEntity);
        httpPost.setHeaders(headers);

        CloseableHttpResponse response = httpClient.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();
        if (responseEntity == null) return null;

        String responseContent = EntityUtils.toString(responseEntity, "UTF-8");
        return JSONObject.parseObject(responseContent);
    }

    public static JSONObject post(String url, String body, ContentType contentType) throws IOException {
        return post(url, body, contentType, new Header[0]);
    }

    public static JSONObject get(String url, Header[] headers) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeaders(headers);

        CloseableHttpResponse response = httpClient.execute(httpGet);
        HttpEntity responseEntity = response.getEntity();
        if (responseEntity == null) return null;

        String responseContent = EntityUtils.toString(responseEntity, "UTF-8");
        return JSONObject.parseObject(responseContent);
    }

    public static JSONObject get(String url) throws IOException {
        return get(url, new Header[0]);
    }
}
