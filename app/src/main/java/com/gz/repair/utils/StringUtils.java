package com.gz.repair.utils;

/**
 * Created by Endeavor on 2016/8/30.
 */
public class StringUtils {

    public static String str2Time(String longTime) {
        if (longTime.length() > 16) {

            try {

                String t1 = longTime.substring(0, 16);
                String time = t1.replace('T', ' ').replace('-', '/');
                return time;
            }catch (Exception e){
                return longTime;
            }
        }
        return longTime;
    }

    public static String string2Time(String longTime) {

        try {
            String t1 = longTime.substring(0, 16);
            String time = t1.replace('-', '/');
            return time;

        }catch (Exception e){
            return longTime;
        }

    }

}
