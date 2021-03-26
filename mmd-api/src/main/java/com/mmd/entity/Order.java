package com.mmd.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
@Data
@Accessors(chain = true)
@TableName("fyt_order")
public class Order  {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户
     */
    private Integer uid;

    /**
     * 推拿师
     */
    private Integer tid;

    /**
     * 产品id
     */
    private Integer pid;

    /**
     * 创建时间
     */
    private Integer createtime;

    /**
     * 状态，-1取消,1待支付,2待派单,3待服务,4服务中,5已完成,6支付超时已取消,7待确认,8待退款,9已出发
     */
    private String zt;

    /**
     * 购买数量
     */
    private Integer sl;

    /**
     * 单价
     */
    private BigDecimal je;

    /**
     * 地址id
     */
    private Integer did;

    private Integer rq;

    private String sj;

    /**
     * 夜间交通费
     */
    private BigDecimal yjjtmoeny;

    /**
     * 折扣
     */
    private BigDecimal zk;

    /**
     * 10公里交通费
     */
    private BigDecimal sgljtmoney;

    /**
     * 总金额
     */
    private BigDecimal zje;

    /**
     * 会员卡支付金额
     */
    private BigDecimal hykmoney;

    /**
     * 支付类型，wxpay,alipay
     */
    private String paymenttype;

    private String remark;

    /**
     * 折扣卡id
     */
    private Integer zkid;

    private Integer sh;

    private Integer cf;

    private Integer cfsj;

    /**
     * 1推广订单 2真实订单 3企业推广订单
     */
    private Integer type;

    /**
     * 分配时间
     */
    private Integer assigntime;

    private BigDecimal promotionmoney;

    private BigDecimal settleprice;

    /**
     * 下单来源
     */
    private String way;

    private Integer recommendid;

    private BigDecimal levelmoney;

    private Integer massagelevel;

    /**
     * 门店id
     */
    private Integer sid;

    private String massagersex;

    /**
     * 性别费用
     */
    private BigDecimal sexmoney;

    /**
     * 连单折扣
     */
    private BigDecimal numdiscount;

    @TableField("inRemark")
    private String inRemark;



}
