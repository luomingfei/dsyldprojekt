package com.mmd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("fyt_product")
public class Product extends BaseEntity<Product> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
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
    @TableField("saleType")
    private Integer saleType;

    /**
     * 最大预定时间
     */
    @TableField("maxTime")
    private Integer maxTime;

    /**
     * 最小预定时间
     */
    @TableField("minTime")
    private Integer minTime;

    @TableField("originalPrice")
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

    @TableField("isRecommend")
    private Integer isRecommend;

    private Integer category;

    private Integer pjnum;

    private List<LevelInfo> levelInfos;

    private List<NumPromotion> numPromotions;
    private ProductSexEntity productSex;
    private Promotion promotion;
    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
