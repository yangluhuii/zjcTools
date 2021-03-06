package com.android.zjctools.utils;

import android.content.Context;

import java.util.Arrays;
import java.util.List;

public class ZStr {

    /**
     * 借用牛逼哄哄的 lzan13 代码
     */


    /**
     * 字符串格式化代码
     *
     * %s   字符串      mingrisoft
     * %c   字符        m
     * %b   布尔        true
     * %d   整数十进制   99
     * %x   整数十六进制 FF
     * %o   整数八进制
     * 77
     * %f   浮点数        99.99
     * %a   浮点数十六进制 FF.35AE
     * %e   指数          9.38e+5
     * %g   通用浮点类型（f和e类型中较短的）
     * %h   散列码
     * %%   百分比
     * ％
     * %n   换行符
     * %tx  日期与时间类型（x代表不同的日期与时间转换符
     */
    /**
     * 字符串转数组
     *
     * @param string    字符串
     * @param separator 分隔符
     * @return 数组
     */
    public static String[] strToArray(String string, String separator) {
        return string.split(separator);
    }

    /**
     * 字符串数组转字符串
     *
     * @param array     字符串数组
     * @param separator 分隔符
     * @return 字符串
     */
    public static String arrayToStr(String[] array, String separator) {
        if (array == null || array.length == 0) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append(array[i]);
        }
        return sb.toString();
    }

    /**
     * 字符串数组转集合
     *
     * @param array 字符串数组
     * @return 集合
     */
    public static List<String> arrayToList(String[] array) {
        return Arrays.asList(array);
    }

    /**
     * 集合转字符串拼接
     *
     * @param list     集合
     * @param splitStr 分隔符
     */
    public static String listToStr(List<String> list, String splitStr) {
        if (list == null || list.size() == 0) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append(list.get(i));
        }
        return sb.toString();
    }

    /**
     * 字符串集合转数组
     *
     * @param list 字符串集合
     * @return 数组
     */
    public static String[] listToArray(List<String> list) {
        return list.toArray(new String[list.size()]);
    }

    /**
     * 根据参数格式化字符串
     */
    public static String byArgs(String str, Object... args) {
        return String.format(str, args);
    }

    /**
     * 根据资源 id 获取字符串
     */
    public static String byRes(int resId) {
        return byRes(ZTools.getContext(), resId);
    }

    /**
     * 根据资源 id 获取字符串
     */
    public static String byRes(Context context, int resId) {
        return context.getString(resId);
    }

    /**
     * 根据资源 id 格式化字符串
     */
    public static String byResArgs(int resId, Object... args) {
        return byResArgs(ZTools.getContext(), resId, args);
    }

    /**
     * 根据资源 id 格式化字符串
     */
    public static String byResArgs(Context context, int resId, Object... args) {
        return context.getString(resId, args);
    }

    /**
     * 检测字符串是否为空白字符串
     */
    public static boolean isEmpty(CharSequence str) {
        if (str == null || "".equals(str)) {
            return true;
        }
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }
}
