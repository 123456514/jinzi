package com.jinzi.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinzi.web.model.entity.PaymentInfo;
import com.jinzi.web.model.vo.PaymentInfoVo;



public interface PaymentInfoService extends IService<PaymentInfo> {
    /**
     * 创建付款信息
     *
     * @param paymentInfoVo 付款信息vo
     * @return boolean
     */
    boolean createPaymentInfo(PaymentInfoVo paymentInfoVo);
}
