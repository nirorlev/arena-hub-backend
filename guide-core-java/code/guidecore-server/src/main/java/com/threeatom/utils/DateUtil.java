//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtil {
    public DateUtil() {
    }

    public static boolean isCurrentMonth(String date, String month) {
        String formatMonth = "yyyy-MM";
        String formatDate = "yyyy-MM-dd";
        SimpleDateFormat sdfMonth = new SimpleDateFormat(formatMonth);
        SimpleDateFormat sdfDate = new SimpleDateFormat(formatDate);

        try {
            Date monthDate = sdfMonth.parse(month);
            Date dayDate = sdfDate.parse(date);
            if (getYearMonth(dayDate) == getYearMonth(monthDate)) {
                return true;
            }
        } catch (ParseException var8) {
            var8.printStackTrace();
        }

        return false;
    }

    public static int getYearMonth(Date date) {
        Calendar calder = Calendar.getInstance();
        calder.setTime(date);
        int year = calder.get(1);
        int month = calder.get(2);
        return year * 100 + month;
    }

    public static boolean isValidDate(String str, String formatString) {
        SimpleDateFormat format = new SimpleDateFormat(formatString);

        try {
            format.setLenient(false);
            format.parse(str);
            return true;
        } catch (ParseException var4) {
            return false;
        }
    }
    public static List<String> getDayOfWeekWithinDateIntervalByWeekDayList(String dataBegin, String dataEnd, List<Integer> weekDayList) {
    	List<String> allList = new ArrayList<>();
    	for(int i=0;i<weekDayList.size();i++) {
    		allList.addAll(getDayOfWeekWithinDateInterval(dataBegin, dataEnd, weekDayList.get(i)));
    	}
    	return allList;
    }
    
    /**
     * 获取某段时间内的周一（二等等）的日期
     * @param dataBegin 开始日期
     * @param dataEnd 结束日期
     * @param weekDays 获取周几，1－6代表周一到周六。0代表周日
     * @return 返回日期List
     */
    public static List<String> getDayOfWeekWithinDateInterval(String dataBegin, String dataEnd, int weekDays) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<String> dateResult = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        String[] dateInterval = {dataBegin, dataEnd};
        Date[] dates = new Date[dateInterval.length];
        for (int i = 0; i < dateInterval.length; i++) {
            String[] ymd = dateInterval[i].split("[^\\d]+");
            cal.set(Integer.parseInt(ymd[0]), Integer.parseInt(ymd[1]) - 1, Integer.parseInt(ymd[2]));
            dates[i] = cal.getTime();
        }
        for (Date date = dates[0]; date.compareTo(dates[1]) <= 0; ) {
            cal.setTime(date);
            if (cal.get(Calendar.DAY_OF_WEEK) - 1 == weekDays) {
                String format = sdf.format(date);
                dateResult.add(format);
            }
            cal.add(Calendar.DATE, 1);
            date = cal.getTime();
        }
        return dateResult;
    }
    
    public static String beforeMonth(String month){
    	DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM");
    	YearMonth yearMonth = YearMonth.parse(month);
    	return yearMonth.minus(1, ChronoUnit.MONTHS).toString();
    }
    
    public static String nextMonth(String month){
    	DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM");
    	YearMonth yearMonth = YearMonth.parse(month);
    	return yearMonth.plus(1, ChronoUnit.MONTHS).toString();
    }
}
