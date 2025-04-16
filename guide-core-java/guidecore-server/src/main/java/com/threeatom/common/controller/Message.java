package com.threeatom.common.controller;

import com.alibaba.fastjson.JSONObject;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;

public class Message {
    private Map<String, Object> meta = new HashMap();
    private Map<String, Object> data;
    private Map<String, Object> release;
    private Map<String, Object> site;
    private Map<String, Object> admin;

    public Message() {}

    public Map<String, Object> getMeta() {
        return this.meta;
    }

    public Message setMeta(Map<String, Object> meta) {
        this.meta = meta;
        return this;
    }

    public Map<String, Object> getData() {
        return this.data;
    }

    public Message setData(Map<String, Object> data) {
        this.data = data;
        return this;
    }

    public Message mergeJson(JSONObject jsonObject) {
        new Exception().printStackTrace(System.out);
        jsonObject
                .keySet()
                .forEach(
                        key -> {
                            if (this.release == null) {
                                this.release = new HashMap();
                                Object value = jsonObject.get(key);
                                this.release.put(key, value);
                            }
                            if (this.site == null) {
                                this.site = new HashMap();
                                Object value = jsonObject.get(key);
                                this.site.put(key, value);
                            }
                            if (this.admin == null) {
                                this.admin = new HashMap();
                                Object value = jsonObject.get(key);
                                this.admin.put(key, value);
                            }
                        });
        return this;
    }

    public Message setJsonData(JSONObject jsonObject) {
        if (this.data == null) {
            this.data = new HashMap();
            jsonObject
                    .keySet()
                    .forEach(
                            key -> {
                                Object value = jsonObject.get(key);
                                this.data.put(key, value);
                            });
        }
        return this;
    }

    public Message addJson(JSONObject jsonObject) {
        jsonObject
                .keySet()
                .forEach(
                        key -> {
                            Object value = jsonObject.get(key);
                            this.meta.put(key, value);
                        });
        return this;
    }

    public Message addMeta(String key, Object object) {
        this.meta.put(key, object);
        return this;
    }

    public Message addData(String key, Object object) {
        if (this.data == null) {
            this.data = new HashMap();
        }

        this.data.put(key, object);
        return this;
    }

    public Message ok() {
        this.addMeta("success", Boolean.TRUE);
        this.addMeta("code", 200);
        this.addMeta("msg", "");
        this.addMeta("timestamp", new Timestamp(System.currentTimeMillis()));
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.addMeta("systemTime", df.format(new Date()));
        return this;
    }

    public Message ok(String statusMsg) {
        this.addMeta("success", Boolean.TRUE);
        this.addMeta("code", 200);
        this.addMeta("msg", statusMsg);
        this.addMeta("timestamp", new Timestamp(System.currentTimeMillis()));
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.addMeta("systemTime", df.format(new Date()));
        return this;
    }

    public Message ok(int statusCode, String statusMsg) {
        this.addMeta("success", Boolean.TRUE);
        this.addMeta("code", statusCode);
        this.addMeta("msg", statusMsg);
        this.addMeta("timestamp", new Timestamp(System.currentTimeMillis()));
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.addMeta("systemTime", df.format(new Date()));
        return this;
    }

    public Message error() {
        this.addMeta("success", Boolean.FALSE);
        this.addMeta("code", 404);
        this.addMeta("msg", "");
        this.addMeta("timestamp", new Timestamp(System.currentTimeMillis()));
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.addMeta("systemTime", df.format(new Date()));
        return this;
    }

    public Message masterIdError() {
        this.addMeta("success", Boolean.FALSE);
        this.addMeta("code", 999);
        this.addMeta("msg", "");
        this.addMeta("timestamp", new Timestamp(System.currentTimeMillis()));
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.addMeta("systemTime", df.format(new Date()));
        return this;
    }

    public Message exception() {
        this.addMeta("runtimeException", Boolean.FALSE);
        this.addMeta("code", 500);
        this.addMeta("msg", "RuntimeException");
        this.addMeta("timestamp", new Timestamp(System.currentTimeMillis()));
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.addMeta("systemTime", df.format(new Date()));
        return this;
    }

    public Message error(String statusMsg) {
        this.addMeta("success", Boolean.FALSE);
        this.addMeta("code", 404);
        this.addMeta("msg", statusMsg);
        this.addMeta("timestamp", new Timestamp(System.currentTimeMillis()));
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.addMeta("systemTime", df.format(new Date()));
        return this;
    }

    public Message errorFrontEnd(String statusMsg) {
        this.addMeta("success", Boolean.FALSE);
        this.addMeta("code", MessageStatusCode.FrontEndError);
        this.addMeta("msg", statusMsg);
        this.addMeta("timestamp", new Timestamp(System.currentTimeMillis()));
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.addMeta("systemTime", df.format(new Date()));
        return this;
    }

    public Message error(int statusCode, String statusMsg) {
        this.addMeta("success", Boolean.FALSE);
        this.addMeta("code", statusCode);
        this.addMeta("msg", statusMsg);
        this.addMeta("timestamp", new Timestamp(System.currentTimeMillis()));
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.addMeta("systemTime", df.format(new Date()));
        return this;
    }

    public Message commonError(int statusCode, String statusMsg, Exception e) {
        this.addMeta("success", Boolean.FALSE);
        this.addMeta("code", statusCode);
        this.addMeta("msg", statusMsg);
        this.addMeta("timestamp", new Timestamp(System.currentTimeMillis()));
        this.addData("e", e.getMessage());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.addMeta("systemTime", df.format(new Date()));
        return this;
    }

    public Message validationError(String statusMsg, Map<String, String> constraints) {
        this.addMeta("success", Boolean.FALSE);
        this.addMeta("code", HttpStatus.BAD_REQUEST.value());
        this.addMeta("msg", statusMsg);
        this.addMeta("timestamp", new Timestamp(System.currentTimeMillis()));
        this.addData("constraints", constraints);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.addMeta("systemTime", df.format(new Date()));
        return this;
    }
}
