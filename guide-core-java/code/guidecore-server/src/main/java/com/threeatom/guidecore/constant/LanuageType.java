package com.threeatom.guidecore.constant;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @title: LanuageType
 * @projectName guidecore
 * @description: TODO
 * @date 2021/11/5/00514:29
 */
public enum LanuageType {
    ch("ch","chinese","zh"),//汉语
    en("en","english","en"),//英语
    ja("ja","japanese","jp"),//日语
    kor("kor","korean","kor"),//韩语
    ru("ru","russian","ru"),//俄语
    fr("fr","french","fra"),//法语
    span("span","spanish","spa"),//西班牙语
    ge("ge","german","de"),//德语
    ar("ar","arabic","ara"),//阿拉伯语
    ita("ita","italian","it"),//意大利语
    por("por","portuguese","pt"),//葡萄牙语
    ben("ben","bengali","ben"),//孟加拉语
    mala("mala","malaysian","may"),//马来西亚语
    ur("ur","urdu","urd"),//乌尔都语
    hin("hin","hindi","hi"),//印地语
    indon("indon","indonesia","id");//印度尼西亚语;





    private String code;
    private String desc;
    private String bdCode;


    LanuageType(String code, String desc,String bdCode){
        this.code=code;
        this.desc=desc;
        this.bdCode=bdCode;
    }

    public String getBdCode() {
        return bdCode;
    }

    public void setBdCode(String bdCode) {
        this.bdCode = bdCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    /***
     * 根据CODE获取枚举实例
     * @param code
     * @return
     */
    public static LanuageType getByCode(String code) {
        for (LanuageType enumType : values()) {
            if (enumType.getCode().equals(code)) {
                return enumType;
            }
        }
        return null;
    }

    public static JSONArray getByDesc1(JSONArray desc) {
        for(int i=0;i<desc.size();i++){
            String desc1 = desc.get(i).toString();
            String desc2 = getByDesc(desc1).toString();
            desc.set(i,desc2);
        }

        return desc;
    }

    public static JSONArray getByCode1(JSONArray code) {
        for(int i=0;i<code.size();i++){
            String code1 = code.get(i).toString();
            String code2 = getByCode(code1).toString();
            code.set(i,code2);
        }
        return code;
    }

    public static JSONArray getDescByCodeJson(JSONArray code) {
        for(int i=0;i<code.size();i++){
            String code1 = code.get(i).toString();
            LanuageType desc = getByCode(code1);
            code.set(i,desc.desc);
        }
        return code;
    }

    public static LanuageType getByDesc(String desc) {
        for (LanuageType enumType : values()) {
            if (enumType.getDesc().equals(desc)) {
                return enumType;
            }
        }
        return null;
    }

    public static LanuageType getByBdCode(String bdCode){
        for (LanuageType enumType : values()) {
            if (enumType.getBdCode().equals(bdCode)) {
                return enumType;
            }
        }
        return null;
    }

    /**
     * 获取除源语言外所有翻译语言
     * @param code
     * @return
     */
    public static List<String> selectNotCode(String code){
        List<String> list = new ArrayList<>();
        for (LanuageType enumType : values()) {
            if (!enumType.getCode().equals(code)) {
                list.add(enumType.getBdCode());
            }
        }
        return list;
    }

    public static List<String> getByCodeList(List<String> codeList){
        List<String> list = new ArrayList<>();
        for (LanuageType enumType : values()) {
            /*if (!enumType.getCode().equals(code)) {
                list.add(enumType.getBdCode());
            }*/
            if (codeList.contains(enumType.getCode())){
                list.add(enumType.getBdCode());
            }
        }
        return list;
    }



}
