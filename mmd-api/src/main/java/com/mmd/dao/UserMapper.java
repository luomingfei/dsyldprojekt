package com.mmd.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mmd.entity.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: qiWen.xue
 * @Email: xqwQAQwq@163.com
 * @Date: 2020/3/27 14:27
 */
public interface UserMapper  extends BaseMapper<MyUser> {

    String findUserType(@Param("phone") String phone);

    String findUserIdByName(@Param("username") String username);

    void cid(@Param("uid") String uid,@Param("cid") String cid);

    Integer isBlackUser( @Param("uid") String uid);

    String findOpenIdByUid(@Param("uid")String uid);

    void UpdateUidByOpenid(@Param("uid")int uid, @Param("openid") String openId, @Param("nickname") String nickname, @Param("city") String city, @Param("headimgurl") String headimgurl, @Param("country") String country, @Param("province") String province);

    void UpdateUidBynewOpenid(@Param("uid")int uid, @Param("openid") String openId);

    List<City> findCitys();

    void addUser(@Param("c") MyUser user);

    int updateOpenidByUid(@Param("uid")int uid, @Param("openid") String openId);

    String findUserNameById(@Param("uid")String uid);

    int AddEstimation(@Param("e")Estimation estimation);

    Estimation findEstimation(@Param("uid")String uid, @Param("oid")Integer oid);

    int UpdateOrderZt(@Param("orderId") String paramString1, @Param("ztNow") String paramString2,
                      @Param("ztTo") String paramString3);

    int UpdateOrderZt4Assign(@Param("orderId") String paramString1, @Param("ztNow") String paramString2,
                             @Param("ztTo") String paramString3);

    List<TagGroupedEntity> findTagsGrouped(@Param("tid") String tid);

    int findTagCount(@Param("tid") String tid, @Param("tag") String tag);
}
