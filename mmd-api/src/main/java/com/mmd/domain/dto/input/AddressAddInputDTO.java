package com.mmd.domain.dto.input;

import lombok.Data;

@Data
public class AddressAddInputDTO {
    private int id;
    private String addressDetail;
    private String contact;
    private String uid;
    private String mph;
    private String phone;
    private String lat;
    private String lng;
}
