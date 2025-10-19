package com.nexus.common.utils;

import cn.hutool.core.date.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 日期工具类
 *
 * @author wk
 * @date 2023/3/12
 */
public class DateUtils extends DateUtil {

    /**
     * 获取当前时间上一周的所有日期
     * 返回结果是一个List，包含 上周的所有日期
     *
     * @param format 格式
     * @return {@link List}<{@link String}>
     */
    public static List<String> getLastWeek(String format) {
        return getLastWeek(new Date(),format);
    }

    /**
     * 获取指定时间的上一周的所有日期
     * 返回结果是一个List，包含 上周的所有日期
     *
     * @param format 格式
     * @return {@link List}<{@link String}>
     */
    public static List<String> getLastWeek(Date currentDate,String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
            dayOfWeek = 7;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(StringUtils.isNotBlank(format)){
            sdf = new SimpleDateFormat(format);
        }
        int offset;
        List<String> resultList = new ArrayList<>();
        for (int i = 1; i <= 7 ; i++) {
            Calendar instance = Calendar.getInstance();
            instance.setTime(currentDate);
            offset = i - dayOfWeek;
            instance.add(Calendar.DATE,offset - 7);
            resultList.add(sdf.format(instance.getTime()));
        }
        return resultList;
    }

    /**
     *
     * 获取上周的一周时间间隔
     * 返回结果是一个Map，包含 上周一的日期和上周日的日期
     *
     * @param format 格式
     * @return {@link Map}<{@link String},{@link String}>
     */
    public static Map<String,String> getLastWeekInterval(String format) {
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        int dayOfWeek = calendar1.get(Calendar.DAY_OF_WEEK) - 1;
        int offset1 = 1 - dayOfWeek;
        int offset2 = 7 - dayOfWeek;
        calendar1.add(Calendar.DATE, offset1 - 7);
        calendar2.add(Calendar.DATE, offset2 - 7);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(StringUtils.isNotBlank(format)){
            sdf = new SimpleDateFormat(format);
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("lastMonday",sdf.format(calendar1.getTime()));
        map.put("lastSunday",sdf.format(calendar2.getTime()));
        return map;
    }

    /**
     * 获取当前日期的一周前的日期
     *
     * @param currentDate 当前日期
     * @return {@link String}
     */
    public static String getLastTime(String currentDate,String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(StringUtils.isNotBlank(format)){
            sdf = new SimpleDateFormat(format);
        }
        Date date = sdf.parse(currentDate);
        return getLastTime(date,format);
    }

    /**
     * 获取当前日期的一周前的日期
     *
     * @param currentDate 当前日期
     * @return {@link String}
     */
    public static String getLastTime(Date currentDate,String format){
        Calendar instance = Calendar.getInstance();
        instance.setTime(currentDate);
        instance.add(Calendar.DATE,-7);
        Date time = instance.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(StringUtils.isNotBlank(format)){
            sdf = new SimpleDateFormat(format);
        }
        return sdf.format(time);
    }

    /**
     *
     * 获取当前日期是星期几
     *
     * @param currentDate 当前日期
     * @return {@link String}
     */
    public static String getDayOfWeek(String currentDate,String format) throws ParseException {
        if(StringUtils.isBlank(format)){
            throw new RuntimeException("必须指定时间格式化方式");
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = sdf.parse(currentDate);
        return getDayOfWeek(date);
    }

    /**
     *
     * 获取当前日期是星期几
     *
     * @param currentDate 当前日期
     * @return {@link String}
     */
    public static String getDayOfWeek(Date currentDate){
        Calendar instance = Calendar.getInstance();
        instance.setTime(currentDate);
        int index = instance.get(Calendar.DAY_OF_WEEK) - 1;
        String[] weeks = new String[]{"周日","周一","周二","周三","周四","周五","周六"};
        return weeks[index];
    }


    /**
     * 获取年月日
     * 返回格式：年月日，无连接符
     * 返回示例：20230214
     * @param date 日期
     * @return {@link String}
     */
    public static String getYearMonthDay(Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(date);
    }
}