package com.threeatom.guidecore.util;


import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class stringWidthConvertUtil {

    public static String chineseStringWidthConvert(String test1){
        char[] chars_test1 = test1.toCharArray();
        String hanz="";
        for (int i = 0; i < chars_test1.length; i++) {
            String temp = String.valueOf(chars_test1[i]);

            Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
            Matcher m = p.matcher(temp);

            // 判断是中文字符
            if (temp.matches("[^\\x00-\\xff]")&&m.find()) {
                hanz+=temp;
               // System.out.println("全角   中文  " + temp);
            }else if (temp.matches("[^\\x00-\\xff]")){
               // System.out.println("全角   " + temp);
                temp = Normalizer.normalize(temp, Normalizer.Form.NFKC);
                hanz += Pattern.compile("[^\\p{ASCII}]").matcher(temp).replaceAll("");
                //System.out.println("全角2   " + temp);
            }
            // 判断是半角字符
            else {
               // System.out.println("半角    " + temp);
                hanz+=temp;
            }
        }
        return hanz;
    }


    public static void main(String[] args) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher("lll");
        System.out.println(m.find());
    }
    public static String stringWidthConvert(String zenkaku) {
        if (zenkaku==null){
            return null;
        }
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(zenkaku);
        boolean isFullwidth = zenkaku.matches(".*[\\u0020-\\u007E].*") && zenkaku.length() <= 20;
        String hankaku="";
        if (m.find()) return zenkaku;
        if (isFullwidth) {
           // System.out.println("字符串符合第一个格式（半角字符）。");
            return zenkaku;
        } else {
            // System.out.println("====="+zenkaku);
            //System.out.println("字符串不符合第一个格式（全角字符）。");
            zenkaku = Normalizer.normalize(zenkaku, Normalizer.Form.NFKC);
            hankaku = Pattern.compile("[^\\p{ASCII}]").matcher(zenkaku).replaceAll("");
            //System.out.println("====="+hankaku);
            return hankaku;
        }
    }

}