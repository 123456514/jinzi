

package com.jinzi.web.model.dto.generator;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 删除代码生成器缓存请求
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Data
public class GeneratorDelCacheRequest implements Serializable {
    
    /**
     * 生成器 ID
     */
    private List<Long> ids;

    private static final long serialVersionUID = 1L;
}
