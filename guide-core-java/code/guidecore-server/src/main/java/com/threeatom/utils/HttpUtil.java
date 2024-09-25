package com.threeatom.utils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.threeatom.common.exception.SystemException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@UtilityClass
public class HttpUtil {

    public static String doPost(String url, Map<String, String> header, String body) {
        StringBuilder result = new StringBuilder();
        HttpURLConnection httpURLConnection = null;

        try {
            URL realUrl = new URL(url);
            httpURLConnection = (HttpURLConnection) realUrl.openConnection();

            for (Map.Entry<String, String> entry : header.entrySet()) {
                httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            try (PrintWriter out = new PrintWriter(httpURLConnection.getOutputStream())) {
                out.print(body);
                out.flush();
            }

            if (HttpURLConnection.HTTP_OK != httpURLConnection.getResponseCode()) {
                return null;
            }

            try (BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    result.append(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return result.toString();
    }

    public static String sendPostFormUrlencoded(String url, Map<String, String> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> forms = new LinkedMultiValueMap<>();
        for (Map.Entry<String, String> entry : body.entrySet()) {
            forms.put(entry.getKey(), Collections.singletonList(entry.getValue()));
        }

        org.springframework.http.HttpEntity<MultiValueMap<String, String>> httpEntity =
            new org.springframework.http.HttpEntity<>(forms, headers);

        return restTemplate.postForObject(url, httpEntity, String.class);
    }

    public static JSONObject doGetStr(String url) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String content = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                return JSONObject.parseObject(content);
            }
        }

        return null;
    }

    public static JSONObject doPostStr(String url, Map<String, String> body) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);

            List<NameValuePair> parameters = new ArrayList<>();
            if (body != null) {
                for (Map.Entry<String, String> entry : body.entrySet()) {
                    parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
            }

            httpPost.setEntity(new UrlEncodedFormEntity(parameters, StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String resContent = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                    return JSONObject.parseObject(resContent);
                }
            }
        }

        return null;
    }

    public static JSONObject post(String url, String body, ContentType contentType, Header[] headers)
        throws SystemException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            StringEntity requestEntity = new StringEntity(body, contentType);
            httpPost.setEntity(requestEntity);
            return request(headers, httpPost, httpClient);
        } catch (IOException e) {
            String msg = "Error sending POST request";
            log.error(msg);
            throw new SystemException(msg);
        }
    }

    public static JSONObject post(String url, String body, ContentType contentType) throws SystemException {
        return post(url, body, contentType, new Header[0]);
    }

    public static JSONObject get(String url, Header[] headers) throws SystemException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            return request(headers, httpGet, httpClient);
        } catch (IOException e) {
            String msg = "Error sending GET request";
            log.error(msg);
            throw new SystemException(msg);
        }
    }

    private static JSONObject request(Header[] headers, HttpRequestBase httpRequest, CloseableHttpClient httpClient)
        throws IOException {
        httpRequest.setHeaders(headers);

        try (CloseableHttpResponse response = httpClient.execute(httpRequest)) {
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity == null) {
                return null;
            }
            String responseContent = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
            return JSONObject.parseObject(responseContent);
        } catch (JSONException e) {
            String message = "Error parsing JSON response";
            log.error(message);
            throw new SystemException(message);
        }
    }

    public static JSONObject get(String url) throws SystemException {
        return get(url, new Header[0]);
    }
}
