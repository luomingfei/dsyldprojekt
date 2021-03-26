package com.mmd.dao;

import com.mmd.entity.Openid;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;

/**
 * @Author: qiWen.xue
 * @Email: xqwQAQwq@163.com
 * @Date: 2020/3/27 15:49
 */
@Mapper
public interface OpenidMapper {

    @Insert("insert into fyt_openid(openid,miniappId,uid,nickname,sex,city,province,country,headimgurl,createtime) values (#{u.openid},#{u.miniappId},#{u.uid},#{u.nickname},#{u.sex},#{u.city},#{u.province},#{u.country},#{u.headimgurl},#{createtime})")
    void AddOpenId(@Param("u") Openid paramUser, @Param("createtime") String paramString);

    Openid findByUid(@Param("uid") Integer uid);

    Openid findByOpenId(@Param("openid") String uid);

    int clearUidByOpenid(@Param("openid") String openid);

}
