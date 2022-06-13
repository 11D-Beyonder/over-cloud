package org.joe.cloud.util;

import java.util.Date;

/**
 * 获取时间的字符串形式
 *
 * @author Tianze Zhu
 * @since 2022-05-07
 */
public class DateTimeUtil {
    public static String getCurrentTime() {
        // %tF 日期 %tT时间 %tA星期 %tY年 %tM月 %tD日。 其中的“<”表示引用前一的参数
        return String.format("%tF %<tT", new Date());
    }

    public static String getDateTime(long millis) {
        // %tF 日期 %tT时间 %tA星期 %tY年 %tM月 %tD日。 其中的“<”表示引用前一的参数
        return String.format("%tF %<tT", new Date(millis));
    }
}
