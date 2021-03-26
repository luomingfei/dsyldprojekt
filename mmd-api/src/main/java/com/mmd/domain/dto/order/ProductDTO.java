package com.mmd.domain.dto.order;

import com.mmd.entity.LevelInfo;
import com.mmd.entity.NumPromotion;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class ProductDTO implements Serializable {
    private String id;
    private String xmmc;
    private String jg;
    private String fzsj;
    private String fwnr;
    private String qd;
    private String qdmax;
    private String tp;
    private Integer ljxd;
    private String sy;
    private String saleType;
    private String originalPrice;
    private Integer maxTime;
    private Integer minTime;
    private String salary;
    private PromotionDTO promotion;
    private Integer type;
    private Integer pjnum;
    private List<LevelInfo> levelInfos;
    private List<NumPromotion> numPromotions;
    private ProductSexDTO productSex;
}

