package com.jinzi.web.controller;

 import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
 import com.jinzi.web.annotation.CheckBanStatus;
 import com.jinzi.web.common.BaseResponse;
 import com.jinzi.web.common.ErrorCode;
 import com.jinzi.web.common.ResultUtils;
 import com.jinzi.web.exception.BusinessException;
 import com.jinzi.web.exception.ThrowUtils;
 import com.jinzi.web.manager.RedisLimiterManager;
 import com.jinzi.web.model.dto.aiassistant.GenChatByAiRequest;
 import com.jinzi.web.model.entity.AiAssistant;
 import com.jinzi.web.model.entity.User;
 import com.jinzi.web.service.AiAssistantService;
 import com.jinzi.web.service.UserService;
 import io.github.briqt.spark4j.SparkClient;
 import io.github.briqt.spark4j.constant.SparkApiVersion;
 import io.github.briqt.spark4j.model.SparkMessage;
 import io.github.briqt.spark4j.model.SparkSyncChatResponse;
 import io.github.briqt.spark4j.model.request.SparkRequest;
 import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
 import java.util.ArrayList;
 import java.util.List;

@RestController
@RequestMapping("/aiAssistant")
@Slf4j
public class AiAssitantController {

    @Resource
    private UserService userService;

    @Resource
    private RedisLimiterManager redisLimiterManager;


    @Resource
    private AiAssistantService aiAssistantService;


    @PostMapping("/chat")
    @CheckBanStatus
    public BaseResponse<?> aiAssistant(@RequestBody GenChatByAiRequest genChatByAiRequest, HttpServletRequest request) {

        User loginUser = userService.getLoginUser(request);
        boolean isFull = userService.reduceWalletBalance(loginUser.getId(), 10);
        if(!isFull){
            return ResultUtils.success("积分不足，请签到获取或购买积分");
        }
        SparkClient sparkClient = new SparkClient();
        String questionName = genChatByAiRequest.getQuestionName();
        String questionGoal = genChatByAiRequest.getQuestionGoal();
        String questionType = genChatByAiRequest.getQuestionType();

        // 校验
        if (StringUtils.isBlank(questionName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "问题名称为空");
        }

        if (ObjectUtils.isEmpty(questionType)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "问题类型为空");
        }

        if (StringUtils.isBlank(questionGoal)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "问题分析目标为空");
        }

        // 用户每秒限流
        redisLimiterManager.doRateLimit("Ai_Rate_" + loginUser.getId());
        // 和AI对话
        sparkClient.appid = "587ec41b";
        sparkClient.apiKey = "0bd48335c8aed23c598872abc8adec98";
        sparkClient.apiSecret = "YWVjODYwNDg5NjBlMmQ2MTY4NDg3Mjc3";
        // 消息列表，可以在此列表添加历史对话记录
        List<SparkMessage> messages = new ArrayList<>();
        messages.add(SparkMessage.userContent(questionGoal));
        // 构造请求
        SparkRequest sparkRequest = SparkRequest.builder()
                // 消息列表
                .messages(messages)
                // 模型回答的tokens的最大长度,非必传，默认为2048。
                // V1.5取值为[1,4096]
                // V2.0取值为[1,8192]
                // V3.0取值为[1,8192]
                .maxTokens(2048)
                // 核采样阈值。用于决定结果随机性,取值越高随机性越强即相同的问题得到的不同答案的可能性越高 非必传,取值为[0,1],默认为0.5
                .temperature(0.2)
                // 指定请求版本，默认使用最新3.0版本
                .apiVersion(SparkApiVersion.V3_0)
                .build();
        SparkSyncChatResponse chatResponse = sparkClient.chatSync(sparkRequest);
        AiAssistant aiAssistant = new AiAssistant();
        aiAssistant.setQuestionName(questionName);
        aiAssistant.setQuestionGoal(questionGoal);
        aiAssistant.setQuestionType(questionType);
        aiAssistant.setUserId(loginUser.getId());
        aiAssistant.setQuestionResult(chatResponse.getContent());
        aiAssistant.setQuestionStatus("succeed");
        boolean saveResult = aiAssistantService.save(aiAssistant);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "ai对话保存失败");
        return ResultUtils.success(aiAssistant);
    }
}
