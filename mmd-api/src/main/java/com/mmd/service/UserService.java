package com.mmd.service;

import com.mmd.entity.*;

import java.util.List;

/**
 * @Author: qiWen.xue
 * @Email: xqwQAQwq@163.com
 * @Date: 2020/3/27 14:25
 */
public interface UserService {
    String findUserType(String phone);

    String findUserIdByName(String username);

    String cid(String uid,String cid);

    boolean isBlackUser(String uid);

    String findOpenIdByUid(String uid);

    void updateUidByOpenid(int uid, String openId, String nickname, String city, String headimgurl, String country, String province);

    void UpdateUidBynewOpenid(int uid, String openId);

    int updateOpenidByUid(int uid, String openId);

    int addUser(MyUser user);

    List<City> findCitys();

    String findUserNameById(String uid);

    boolean AddEstimation(Estimation estimation);

    Estimation findEstimation(String uid,Integer oid);

    Boolean UpdateOrderZt(String orderId, String ztNow, String ztTo);

    Boolean UpdateOrderZt4Assign(String orderId, String ztNow, String ztTo);

    List<TagGroupedEntity> findTagsOfMassager(String tid);
}
