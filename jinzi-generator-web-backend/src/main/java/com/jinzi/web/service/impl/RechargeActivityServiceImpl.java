package com.jinzi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.jinzi.web.mapper.RechargeActivityMapper;
import com.jinzi.web.model.entity.RechargeActivity;
import com.jinzi.web.service.RechargeActivityService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class RechargeActivityServiceImpl extends ServiceImpl<RechargeActivityMapper, RechargeActivity>
        implements RechargeActivityService {

    @Override
    public List<RechargeActivity> getRechargeActivityByOrderNo(String orderNo) {
        LambdaQueryWrapper<RechargeActivity> activityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        activityLambdaQueryWrapper.eq(RechargeActivity::getOrderNo, orderNo);
        return this.list(activityLambdaQueryWrapper);
    }
}




