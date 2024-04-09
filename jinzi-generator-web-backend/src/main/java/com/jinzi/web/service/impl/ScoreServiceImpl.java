package com.jinzi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinzi.web.common.ErrorCode;
import com.jinzi.web.exception.ThrowUtils;
import com.jinzi.web.mapper.ScoreMapper;
import com.jinzi.web.model.entity.Score;
import com.jinzi.web.service.ScoreService;
import org.springframework.stereotype.Service;

/**
* @author zhoujin
* @description 针对表【score(积分表)】的数据库操作Service实现
*/
@Service
public class ScoreServiceImpl extends ServiceImpl<ScoreMapper, Score>
    implements ScoreService {

    @Override
    public void checkIn(Long userId) {
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        Score score = this.getOne(queryWrapper);
        ThrowUtils.throwIf(score == null, ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(score.getIsSign()==1,ErrorCode.PARAMS_ERROR,"领取失败，今日已领取");
        Long scoreTotal = score.getScoreTotal();
        UpdateWrapper<Score> updateWrapper = new UpdateWrapper();
        updateWrapper
                //此处暂时写死签到积分
                .eq("userId",userId)
                .set("scoreTotal",scoreTotal+1)
                .set("isSign",1);
        boolean r = this.update(updateWrapper);
        ThrowUtils.throwIf(!r, ErrorCode.OPERATION_ERROR,"更新签到数据失败");
    }

    @Override
    public void deductPoints(Long userId, Long points) {
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        Score score = this.getOne(queryWrapper);
        ThrowUtils.throwIf(score.getScoreTotal()<points,ErrorCode.OPERATION_ERROR,"积分不足，请联系管理员！");
        Long scoreTotal = score.getScoreTotal();
        UpdateWrapper<Score> updateWrapper = new UpdateWrapper();
        updateWrapper
                .eq("userId",userId)
                .set("scoreTotal",scoreTotal-points);
        boolean r = this.update(updateWrapper);
        ThrowUtils.throwIf(!r, ErrorCode.OPERATION_ERROR);
    }

    @Override
    public Long getUserPoints(Long userId) {
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        Score score = this.getOne(queryWrapper);
        return score.getScoreTotal();
    }

    @Override
    public int getIsSign(Long userId){
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        Score score = this.getOne(queryWrapper);
        return score.getIsSign();
    }
}