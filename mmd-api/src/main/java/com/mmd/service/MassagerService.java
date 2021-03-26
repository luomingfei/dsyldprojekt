package com.mmd.service;

import com.mmd.entity.Massager;
import com.mmd.entity.TakeUpTime;

import java.text.ParseException;
import java.util.List;

/**
 * @Author: qiWen.xue
 * @Email: xqwQAQwq@163.com
 * @Date: 2020/4/1 21:40
 */
public interface MassagerService {
    List<TakeUpTime> getTimeRecord(String tid);
    void filterMassagers(List<Massager> massagers, String serviceTime, long beginTime, long endTime, double lat,
                         double lng, Integer did) throws ParseException;

}
