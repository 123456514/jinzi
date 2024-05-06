package com.jinzi.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.jinzi.web.common.BaseResponse;
import com.jinzi.web.common.ErrorCode;
import com.jinzi.web.common.ResultUtils;
import com.jinzi.web.exception.BusinessException;
import com.jinzi.web.model.entity.DailyCheckIn;
import com.jinzi.web.model.entity.User;
import com.jinzi.web.model.vo.UserVO;
import com.jinzi.web.service.DailyCheckInService;
import com.jinzi.web.service.UserService;
import com.jinzi.web.utils.RedissonLockUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 *  签到接口  定时任务
 */
@RestController
@RequestMapping("/dailyCheckIn")
@Slf4j
public class DailyCheckInController {

    @Resource
    private DailyCheckInService dailyCheckInService;

    @Resource
    private UserService userService;
    @Resource
    private RedissonLockUtil redissonLockUtil;


    /**
     * 签到
     *
     * @param request 请求
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @PostMapping("/doCheckIn")
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<Boolean> doDailyCheckIn(HttpServletRequest request) {
        // 判断用户是否登录
        User loginUser = userService.getLoginUser(request);
        // 制作一个分布式锁
        String redissonLock = ("doDailyCheckIn_" + loginUser.getUserAccount()).intern();
        return redissonLockUtil.redissonDistributedLocks(redissonLock, () -> {
            LambdaQueryWrapper<DailyCheckIn> dailyCheckInLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dailyCheckInLambdaQueryWrapper.eq(DailyCheckIn::getUserId, loginUser.getId());
            DailyCheckIn dailyCheckIn = dailyCheckInService.getOne(dailyCheckInLambdaQueryWrapper);
            // 如果在签到表中存在相同的
            if (ObjectUtils.isNotEmpty(dailyCheckIn)) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "签到失败,今日已签到");
            }
            dailyCheckIn = new DailyCheckIn();
            dailyCheckIn.setUserId(loginUser.getId());
            // 签到成功 获得10个积分
            dailyCheckIn.setAddPoints(10);
            // 添加到数据库
            boolean dailyCheckInResult = dailyCheckInService.save(dailyCheckIn);
            // 用户的积分在原来的基础上增加10
            boolean addWalletBalance = userService.addWalletBalance(loginUser.getId(), dailyCheckIn.getAddPoints());
            // 获得交集
            boolean result = dailyCheckInResult & addWalletBalance;
            if (!result) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR);
            }
            return ResultUtils.success(true);
        }, "签到失败");
    }
}
