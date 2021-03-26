package com.mmd.domain.dto.output;

import com.mmd.entity.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: qiWen.xue
 * @Email: xqwQAQwq@163.com
 * @Date: 2020/3/30 11:48
 */
@Data
@Accessors(chain = true)
public class ProductOutputDTO{
    private Long id;

    /**
     * 坐卧标记1坐，2卧
     */
    private String zwbj;

    /**
     * 项目名称
     */
    private String xmmc;

    /**
     * 价格
     */
    private BigDecimal jg;

    /**
     * 分钟时间
     */
    private String fzsj;

    /**
     * 起订
     */
    private Integer qd;

    /**
     * 项目图片
     */
    private String tp;

    /**
     * 服务内容
     */
    private String fwnr;

    /**
     * 分类标记：1，单人；2，双人；3，团体
     */
    private Integer flbj;

    private Integer createtime;

    private String status;

    private Integer qdmax;

    private String city;

    private Integer ljxd;

    /**
     * 0：普通；1：新用户专享；2：限时秒杀到5：30；3：限时秒杀到6：30
     */
    private Integer saleType;

    /**
     * 最大预定时间
     */
    private Integer maxTime;

    /**
     * 最小预定时间
     */
    private Integer minTime;

    private BigDecimal originalPrice;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 0：都有；1：魔魔达；2：点评
     */
    private Integer platform;

    private BigDecimal salary;

    /**
     * 项目类别：0，到家；1，到店
     */
    private Integer type;

    private Integer isRecommend;

    private Integer category;

    private Integer pjnum;

    private List<NumPromotion> numPromotions;
    private ProductSexEntity productSex;
    private Promotion promotion;
}
