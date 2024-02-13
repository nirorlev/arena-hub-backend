package com.threeatom.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * @author Administrator
 * @title: XmlUtil
 * @projectName spring-paypal-example
 * @description: TODO
 * @date 2021/11/24/0249:48
 */
public class XmlUtil {
    // xml解析
    public static Map doXMLParse(String strxml) throws JDOMException, IOException {
        strxml = strxml.replaceFirst("encoding=\".*\"", "encoding=\"UTF-8\"");

        if (null == strxml || "".equals(strxml)) {
            return null;
        }

        Map m = new HashMap();

        InputStream in = new ByteArrayInputStream(strxml.getBytes("UTF-8"));
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(in);
        Element root = doc.getRootElement();
        List list = root.getChildren();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Element e = (Element) it.next();
            String k = e.getName();
            String v = "";
            List children = e.getChildren();
            if (children.isEmpty()) {
                v = e.getTextNormalize();
            } else {
                v = getChildrenText(children);
            }

            m.put(k, v);
        }

        // 关闭流
        in.close();

        return m;
    }

    public static String getChildrenText(List children) {
        StringBuffer sb = new StringBuffer();
        if (!children.isEmpty()) {
            Iterator it = children.iterator();
            while (it.hasNext()) {
                Element e = (Element) it.next();
                String name = e.getName();
                String value = e.getTextNormalize();
                List list = e.getChildren();
                sb.append("<" + name + ">");
                if (!list.isEmpty()) {
                    sb.append(getChildrenText(list));
                }
                sb.append(value);
                sb.append("</" + name + ">");
            }
        }

        return sb.toString();
    }

    public static Map<String, String> soapParse(String result) {
        String[] results = result.split("&");
        Map<String, String> resultMap = new HashMap<>();
        for (int i = 0; i < results.length; i++) {
            String val = results[i];
            resultMap.put(val.substring(0, val.indexOf("=")), val.substring(val.indexOf("=") + 1));
        }
        return resultMap;
    }

    public static List<Map<String, String>> soapListParse(Integer size, String result) {
        String[] results = result.split("&");
        List<Map<String, String>> resultMapList = new ArrayList<>();
        for (int i = 0; i <= size; i++) {
            Map<String, String> resultMap = new HashMap<>();
            for (int j = 0; j < results.length; j++) {
                String str = results[j];
                String[] strs = str.split("=");
                resultMap.put(strs[0], strs[1]);
            }
            resultMapList.add(resultMap);
        }
        return resultMapList;
    }
}
