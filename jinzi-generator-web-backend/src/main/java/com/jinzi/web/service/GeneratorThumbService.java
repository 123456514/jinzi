package com.jinzi.web.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.jinzi.web.model.entity.GeneratorThumb;
import com.jinzi.web.model.entity.User;
import com.jinzi.web.model.vo.UserVO;

/**
 * 生成器点赞服务
 *
 * @author codeZhang
 */
public interface GeneratorThumbService extends IService<GeneratorThumb> {

    /**
     * 点赞
     *
     * @param generatorId
     * @param loginUser
     * @return
     */
    int doGeneratorThumb(long generatorId, UserVO loginUser);

    /**
     * 生成器点赞（内部服务）
     *
     * @param userId
     * @param generatorId
     * @return
     */
    int doGeneratorThumbInner(long userId, long generatorId);
}