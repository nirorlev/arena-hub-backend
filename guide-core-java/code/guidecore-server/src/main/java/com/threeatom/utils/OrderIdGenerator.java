//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.utils;

import java.util.Date;
import org.apache.commons.lang3.RandomStringUtils;

public class OrderIdGenerator {
    public OrderIdGenerator() {
    }

    public static String getOrderNumber() {
        String time = Long.toString((new Date()).getTime());
        String randomString = RandomStringUtils.randomNumeric(8);
        return time + randomString;
    }
}
