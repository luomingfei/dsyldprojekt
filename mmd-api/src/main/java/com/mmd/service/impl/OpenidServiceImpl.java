package com.mmd.service.impl;

import com.mmd.dao.OpenidMapper;
import com.mmd.entity.Openid;
import com.mmd.service.OpenidService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: qiWen.xue
 * @Email: xqwQAQwq@163.com
 * @Date: 2020/3/27 15:39
 */
@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OpenidServiceImpl implements OpenidService {

    private final OpenidMapper openidMapper;

    @Override
    public void AddOpenId(Openid build, String time) {
        openidMapper.AddOpenId(build,time);
    }

    @Override
    public Openid findByUid(Integer uid) {
        return openidMapper.findByUid(uid);
    }

    @Override
    public Openid findByOpenId(String openid) {
        return openidMapper.findByOpenId(openid);
    }

    @Override
    public int clearUidByOpenid(String openid) {
        return openidMapper.clearUidByOpenid(openid);
    }


}
