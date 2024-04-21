package com.jinzi.web.mapstruct;

import com.jinzi.web.model.dto.generator.GeneratorAddRequest;
import com.jinzi.web.model.dto.generator.GeneratorEditRequest;
import com.jinzi.web.model.dto.generator.GeneratorQueryRequest;
import com.jinzi.web.model.dto.generator.GeneratorUpdateRequest;
import com.jinzi.web.model.entity.Generator;
import com.jinzi.web.model.vo.GeneratorVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 代码生成器转化
 */
@Mapper
public interface GeneratorConvert {

    GeneratorConvert INSTANCE = Mappers.getMapper(GeneratorConvert.class);

//    Generator convertGeneratorByAddRequest(GeneratorAddRequest generatorAddRequest);

    Generator convertGeneratorByUpdateRequest(GeneratorUpdateRequest generatorUpdateRequest);

    Generator convertGeneratorByQueryRequest(GeneratorQueryRequest generatorQueryRequest);

    Generator convertGeneratorByEditRequest(GeneratorEditRequest generatorEditRequest);

    GeneratorVO convertGeneratorVOByGenerator(Generator generator);
}
