//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.common;

import com.alibaba.fastjson.JSONArray;
import com.threeatom.common.exception.SystemException;
import java.util.List;

import com.threeatom.guidecore.util.I18NUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

public class ApiAssert extends Assert {
    public ApiAssert() {
    }

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new SystemException(message);
        }
    }

    public static void notNull(Object object, int code, String message) {
        if (object == null) {
            throw new SystemException(code, message);
        }
    }

    public static void notEmpty(String str, String message) {
        if (StringUtils.isEmpty(str)) {
            throw new SystemException(message);
        }
    }

    public static void notEmpty(String str, int code, String message) {
        if (StringUtils.isEmpty(str)) {
            throw new SystemException(code, message);
        }
    }

    public static void assertId(Integer id, String message) {
        if (id == null || id.equals(0) || id < 0) {
            throw new SystemException(message);
        }
    }

    public static void jsonValueIntegerIn(Integer value, String json, String message) {
        if (value == null) {
            throw new SystemException(I18NUtil.get("The validation value cannot be empty") +message);
        } else {
            JSONArray valueArr = JSONArray.parseArray(json);
            if (!valueArr.contains(value)) {
                throw new SystemException(message);
            }
        }
    }
    
    public static void ifStringInList(String value, List<String> list, String message) {
    	 if (value == null) {
             throw new SystemException(I18NUtil.get("The validation value cannot be empty")+message);
         } else {
             if (!list.contains(value)) {
                 throw new SystemException(message);
             }
         }
    }
    
    public static void ifStringNotInList(String value, List<String> list, String message) {
   	 if (value == null) {
            throw new SystemException(I18NUtil.get("The validation value cannot be empty")+message);
        } else {
            if (list.contains(value)) {
                throw new SystemException(message);
            }
        }
   }

    public static void assertIntegerUniqueList(List<Integer> ids) {
        if (ids == null) {
            throw new SystemException(I18NUtil.get("list.error"));
        } else {
            long count = ids.stream().distinct().count();
            if (count < (long)ids.size()) {
                throw new SystemException(I18NUtil.get("id.error"));
            }
        }
    }
}
