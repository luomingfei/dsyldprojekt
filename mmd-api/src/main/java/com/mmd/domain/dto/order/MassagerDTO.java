package com.mmd.domain.dto.order;

import lombok.Data;

import java.util.List;
@Data
public class MassagerDTO implements Comparable<MassagerDTO> {
    private String id;
    private String xm;
    private String xb;
    private String wz;
    private String tx;
    private String fwqy;
    private String gzjy;
    private String location;
    private String ljxd;
    private String pj;
    private String pj_num;
    private String hpl;
    private Double distance;
    private String phone;
    private String openid;
    private String sid;
    private String type;
    private int hoursalary;
    private String password;
    private String age;
    private String city;
    private List<TagGroupedDTO> tags;
    private Double orderrange;
    private Integer level;
    private String status;
    private Integer laterDot;
    private Double laterRange;
    private String jpushRegistrationId;
    private Integer isRecommend;

    @Override
    public int compareTo(MassagerDTO o) {
        return 0;
    }
}
