package com.mmd.utils;


import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * 日期处理
 *
 * @author dsccc
 */
public class DateUtils {
    //计算时间差距分钟
    public static Long computeBetween(Instant startTime, Instant endTime) {
        long between = ChronoUnit.MINUTES.between(startTime, endTime);
        return between;
    }

}
