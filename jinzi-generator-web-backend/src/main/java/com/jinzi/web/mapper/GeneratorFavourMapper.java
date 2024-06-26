package com.jinzi.web.mapper;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinzi.web.model.entity.Generator;
import com.jinzi.web.model.entity.GeneratorFavour;
import org.apache.ibatis.annotations.Param;

/**
 * 代码生成器收藏数据库操作
 *
 */
public interface GeneratorFavourMapper extends BaseMapper<GeneratorFavour> {

    /**
     * 分页查询收藏代码生成器列表
     *
     * @param page
     * @param queryWrapper
     * @param favourUserId
     * @return
     */
    Page<Generator> listFavourGeneratorByPage(IPage<Generator> page, @Param(Constants.WRAPPER) Wrapper<Generator> queryWrapper,
                                              long favourUserId);

}

