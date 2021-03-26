package com.mmd.entity;

import lombok.Data;

import java.util.List;
@Data
public class Massager implements Comparable<Massager> {
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
	private List<TagGroupedEntity> tags;
	private Double orderrange;
	private Integer level;
	private String status;
	private Integer laterDot;
	private Double laterRange;
	private String jpushRegistrationId;
	private Integer isRecommend;

	@Override
	public int compareTo(Massager m) {
		if(this.getIsRecommend() != null && m.getIsRecommend() != null) {
			if (this.getIsRecommend() == 1 && m.getIsRecommend() == 1) {
				if (this.getDistance() > m.getDistance()) {
					return 1;
				} else if (this.getDistance() == m.getDistance()) {
					return 0;
				} else {
					return -1;
				}
			} else if (this.getIsRecommend() == 1 && m.getIsRecommend() == 0) {
				return -1;
			} else if (m.getIsRecommend() == 1 && this.getIsRecommend() == 0){
				return 1;
			}
		}
		if (this.getDistance() > m.getDistance()) {
			return 1;
		} else if (this.getDistance() == m.getDistance()) {
			return 0;
		} else {
			return -1;
		}
	}
}
