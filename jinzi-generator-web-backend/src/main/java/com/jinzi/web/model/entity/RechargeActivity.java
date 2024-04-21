package com.jinzi.web.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 充值活动表
 */
@TableName(value = "recharge_activity")
@Data
public class RechargeActivity implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 微信订单号/支付宝订单id
     */
    private String orderNo;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 商品id
     */
    private Long productId;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;
}