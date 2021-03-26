package com.mmd.domain.dto.comment;

import lombok.Data;

import java.util.Map;

@Data
public class EstimationDTO {
    private String userId;
    private Map<String, Object> map;

}
