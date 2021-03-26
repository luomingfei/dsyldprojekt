package com.mmd.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmd.dao.UserMapper;
import com.mmd.entity.*;
import com.mmd.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: qiWen.xue
 * @Email: xqwQAQwq@163.com
 * @Date: 2020/3/27 14:26
 */
@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, MyUser> implements UserService {

    @Override
    public String findUserType(String phone) {
        return this.baseMapper.findUserType(phone);
    }

    @Override
    public String findUserIdByName(String username) {
        return this.baseMapper.findUserIdByName(username);
    }

    @Override
    public String cid(String uid, String cid) {
         this.baseMapper.cid(uid,cid);
        return "";
    }

    @Override
    public boolean isBlackUser(String uid) {
        Integer isBlack = baseMapper.isBlackUser(uid);
        if (isBlack == null) {
            return false;
        }
        return isBlack.equals(1) ? true : false;
    }

    @Override
    public String findOpenIdByUid(String uid) {
        return this.baseMapper.findOpenIdByUid(uid);
    }

    @Override
    public void updateUidByOpenid(int uid, String openId, String nickname, String city, String headimgurl, String country, String province) {
        this.baseMapper.UpdateUidByOpenid(uid, openId,nickname,city,headimgurl,country,province);
    }

    @Override
    public void UpdateUidBynewOpenid(int uid, String openId) {
        this.baseMapper.UpdateUidBynewOpenid(uid, openId);
    }

    @Override
    public int updateOpenidByUid(int uid, String openId) {
        return this.baseMapper.updateOpenidByUid(uid, openId);
    }

    @Override
    public int addUser(MyUser user) {
        this.baseMapper.addUser(user);
        return user.getId();
    }

    @Override
    public List<City> findCitys() {
        return this.baseMapper.findCitys();
    }

    @Override
    public String findUserNameById(String uid) {
        return this.baseMapper.findUserNameById(uid);
    }

    @Override
    public boolean AddEstimation(Estimation estimation) {
        if (this.baseMapper.AddEstimation(estimation) != 0) {
            return Boolean.valueOf(true);
        }

        return Boolean.valueOf(false);
    }

    @Override
    public Estimation findEstimation(String uid, Integer oid) {
        return this.baseMapper.findEstimation(uid,oid);
    }

    @Override
    public Boolean UpdateOrderZt(String orderId, String ztNow, String ztTo) {
        if (this.baseMapper.UpdateOrderZt(orderId, ztNow, ztTo) == 1){
            return Boolean.valueOf(true);
        }
        return Boolean.valueOf(false);
    }

    @Override
    public Boolean UpdateOrderZt4Assign(String orderId, String ztNow, String ztTo) {
        if (this.baseMapper.UpdateOrderZt4Assign(orderId, ztNow, ztTo) == 1){
            return Boolean.valueOf(true);
        }
        return Boolean.valueOf(false);
    }

    @Override
    public List<TagGroupedEntity> findTagsOfMassager(String tid) {
        List<TagGroupedEntity> result = baseMapper.findTagsGrouped(tid);
        if (result != null)
            for (int i = 0; i < result.size(); i++) {
                result.get(i).setCount(baseMapper.findTagCount(tid, result.get(i).getTag()));
            }
        return result;
    }

}
