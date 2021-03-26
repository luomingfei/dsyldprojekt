package com.mmd.service;

import com.mmd.entity.Openid;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: qiWen.xue
 * @Email: xqwQAQwq@163.com
 * @Date: 2020/3/27 15:39
 */
public interface OpenidService {
    void AddOpenId(Openid build, String time);

    Openid findByUid(@Param("uid") Integer uid);

    Openid findByOpenId(@Param("openid") String openid);

    int clearUidByOpenid(@Param("openid") String openid);
}
