package com.demo.yun.util;


import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class StringUtil
{
    // 四个MMMM显示汉字的月份（一月）
//	public static final SimpleDateFormat ms_SimpleDateFormat = new SimpleDateFormat(
//			"MMMM dd HH:mm", Locale.getDefault());
    // 2个MM显示数字月份（1月）
    public static SimpleDateFormat ms_SimpleDateFormat = null;
    public static SimpleDateFormat ms_TodayDateFormat = null;

    public static boolean isEmpty(String value)
    {
        return isEmpty(value, null);
    }

    public static boolean isNotEmpty(String value)
    {
        return !isEmpty(value);
    }

    public static boolean isEmpty(String value, String ignore)
    {
        if (value == null || value.trim().length() == 0)
        {
            return true;
        }
        else
        {
            if (ignore != null && value.equalsIgnoreCase(ignore))
            {
                return true;
            }
        }

        return false;
    }

    public static boolean isNotEmpty(String value, String ignore)
    {
        return !isEmpty(value, ignore);
    }

    /**
     * 返回Date格式数据
     * @param strDate yyyy-MM-dd
     * @return
     */
    public static Date getDate(String strDate)
    {
        try
        {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            return df.parse(strDate);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static Date getDate(SimpleDateFormat sdf, String strDateTime)
    {
        try
        {
            return sdf.parse(strDateTime);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * 日期转换为字符串
     *
     * @param lDate
     * @return
     */
    public static String formatDate(long lDate)
    {
        try
        {
            Date date = new Date(lDate);
            SimpleDateFormat simpleDateFormat = getSimpleDateFormat(isToday(date));
            String strDate = simpleDateFormat.format(date);
            return strDate;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return "";
    }

    private static SimpleDateFormat getSimpleDateFormat(boolean today)
    {
        try
        {
            if (today)
            {
                if (ms_TodayDateFormat == null)
                {
                    ms_TodayDateFormat = new SimpleDateFormat(
                            "今天 HH:mm", Locale.getDefault());
                }

                return ms_TodayDateFormat;
            }
            else
            {
                if (ms_SimpleDateFormat == null)
                {
                    ms_SimpleDateFormat = new SimpleDateFormat(
                            "MM月dd日 HH:mm", Locale.getDefault());
                }

                return ms_SimpleDateFormat;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 判断给定日期是否是今天内的日期
     * @param date
     * @return
     */
    private static boolean isToday(Date date)
    {
        try
        {
            Date today = new Date();
            Calendar caledarToday = GregorianCalendar.getInstance(Locale.getDefault());
            caledarToday.setTime(today);
            int todayYear = caledarToday.get(Calendar.YEAR);
            int todayMonth = caledarToday.get(Calendar.MONTH);
            int todayDay = caledarToday.get(Calendar.DAY_OF_MONTH);

            Calendar caledarDate = GregorianCalendar.getInstance(Locale.getDefault());
            caledarDate.setTime(date);
            int dateYear = caledarDate.get(Calendar.YEAR);
            int dateMonth = caledarDate.get(Calendar.MONTH);
            int dateDay = caledarDate.get(Calendar.DAY_OF_MONTH);

            if (todayYear == dateYear && todayMonth == dateMonth && todayDay == dateDay)
            {
                return true;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }

    public static String getURLEncode(String value)
    {
        try
        {
            return URLEncoder.encode(value, "UTF-8");
        }
        catch (Exception e)
        {
        }

        return "";
    }

    /**
     * 检查Email地址合法性
     */
    public static boolean checkEmail(String email)
    {
        Pattern pattern = Pattern
                .compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");

        return pattern.matcher(email).matches();
    }

    /**
     * 检测手机号码合法性
     */
    public static boolean checkPhone(String phone)
    {
        Pattern pattern = Pattern.compile("^1[358]\\d{9}$");
        return pattern.matcher(phone).matches();
    }

    /**
     * 检测URL合法性
     */
    public static boolean checkURL(final String strURL)
    {
        String strReg = "^(http://|https://)?((?:[A-Za-z0-9]+-[A-Za-z0-9]+|[A-Za-z0-9]+)\\.)+([A-Za-z]+)[/\\?\\:]?.*$";
        Pattern pattern = Pattern.compile(strReg);
        return pattern.matcher(strURL).matches();
    }

    /**
     * String List 转换成 String 数组
     */
    public static String[] listToArray(List<String> arrayList)
    {
        if (arrayList != null)
        {
            int size = arrayList.size();
            return (String[]) arrayList.toArray(new String[size]);
        }

        return null;
    }

    public static List<String> arrayToList(String[] array)
    {
        ArrayList<String> arrayList = new ArrayList<String>();
        if (array != null)
        {
            for (String str : array)
            {
                arrayList.add(str);
            }
        }
        return arrayList;
    }

    public static String formatSize(long size)
    {
        double kb = 1024.0;
        double mb = kb * kb;
        double gb = mb * mb;
        if (size < kb)
        {
            return size + "B";
        }
        else if (size < mb)
        {
            return String.format(Locale.getDefault(), "%.1f KB", size / kb);
        }
        else if (size < gb)
        {
            return String.format(Locale.getDefault(), "%.1f MB", size / mb);
        }
        else
        {
            return String.format(Locale.getDefault(), "%.1f GB", size / gb);
        }
    }

    /**
     * 格式化时长（00:00:00）
     * @param time 时长（秒）
     * @return
     */
    public static String formatTime(int time)
    {
        try
        {
            int nHour, nMinute, nSecond;
            nHour = time / 3600;
            nMinute = (time - 3600 * nHour) / 60;
            nSecond = (time - 60 * nMinute) % 60;

            String strTalkTime = "";
            if (nHour > 0)
            {
                String strHour = String.format("%02d:", nHour);
                strTalkTime += strHour;
            }

            String strMinute = String.format("%02d:", nMinute);
            strTalkTime += strMinute;

            String strSecond = String.format("%02d", nSecond);
            strTalkTime += strSecond;

            return strTalkTime;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 分割字符串
     *
     * @param str          字符串
     * @param strSeparator 分割符
     * @param strArray     字符串数组
     * @return 字符串数组中元素的个数
     */
    public static int splitString(final String str, final String strSeparator, List<String> strArray)
    {
        try
        {
            int nSize = 0;
            int nStart = 0;

            String strTemp = "";

            if (str == null || strSeparator == null)
            {
                return 0;
            }

            // 长度
            int nLen = str.length();

            if (nLen <= 0)
            {
                return 0;
            }

            if (strSeparator.length() <= 0)
            {
                return 0;
            }

            if (strArray != null)
            {
                // 清空
                strArray.clear();
            }
            else
            {
                strArray = new ArrayList<String>();
            }

            // 查找子字符串的第一个字索引值
            int nPos = str.indexOf(strSeparator);

            if (nPos != -1)
            {
                while (nPos != -1)
                {
                    if (nStart >= 0 && nPos < nLen)
                    {
                        // 提取起始位置位于 nStart 的长度为 nPos - nStart 个字符的子字符串
                        strTemp = str.substring(nStart, nPos);

                        // 在数组的末尾添加一个元素
                        strArray.add(strTemp);

                        nStart = nPos + strSeparator.length();

                        // 从位置 nStart 开始查找子字符串的第一个字索引值
                        nPos = str.indexOf(strSeparator, nStart);
                    }
                }

                // 检测字符的计数值
                if (nStart < nLen)
                {
                    // 提取起始位置位于 nStart 的长度为 nLen - nStart 个字符的子字符串
                    strTemp = str.substring(nStart);

                    // 在数组的末尾添加一个元素
                    strArray.add(strTemp);
                }

                // 获取数组中的元素个数
                nSize = (int) strArray.size();
            }

            return nSize;
        }
        catch (Exception e)
        {
        }

        return 0;
    }
}
