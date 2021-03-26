package com.mmd.domain.dto.order;

import lombok.Data;

@Data
public class AddressDTO {
    private int id;
    private String addressInfomation;
    private String addressDetail;
    private String contact;
    private String uid;
    private String mph;
    private String phone;
    private String lat;
    private String lng;
    private Integer type;
    private String way;
}
