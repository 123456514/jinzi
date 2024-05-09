package com.jinzi.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinzi.web.model.entity.Generator;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author 26702
* @description 针对表【generator(代码生成器)】的数据库操作Mapper
* @createDate 2024-04-05 18:44:08
* @Entity com.jinzi.web.model.entity.Generator
*/
public interface GeneratorMapper extends BaseMapper<Generator> {
    /**
     * 查询所有删除了的 generator
     *
     * @return generator
     */
    @Select("SELECT id, distPath FROM generator WHERE isDelete = 1;")
    List<Generator> listDeletedGenerator();
    /**
     * 查询 hot generator
     * <p>
     * 只查询 TOP 10，其余的在使用之后判断
     *
     * @see GeneratorConstant#HOT_GENERATOR_USE_COUNT_THRESHOLD
     */
    @Select("SELECT id FROM generator WHERE useCount >= 500 AND isDelete = 0 ORDER BY useCount DESC LIMIT 10;")
    List<Long> listHotGeneratorIds();

}




