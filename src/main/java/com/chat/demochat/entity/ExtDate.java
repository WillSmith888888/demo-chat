package com.chat.demochat.entity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExtDate extends Date
{
    private static final Map<String, DateFormat> DATE_FORMAT_MAP = new HashMap<>();

    static
    {
        DATE_FORMAT_MAP.put("yyyy-MM-dd hh:mm:ss", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));
    }

    public static String getCurrentTimeStr()
    {
        return getCurrentTimeStr("yyyy-MM-dd hh:mm:ss");
    }

    public static String getCurrentTimeStr(String format)
    {
        String str;
        DateFormat dateFormat = DATE_FORMAT_MAP.get(format);
        if (dateFormat == null)
        {
            dateFormat = new SimpleDateFormat(format);
            str = dateFormat.format(System.currentTimeMillis());
            DATE_FORMAT_MAP.put(format, dateFormat);
        }
        else
        {
            str = dateFormat.format(System.currentTimeMillis());
        }
        return str;
    }

}
