package com.jinzi.web.model.dto.productinfo;


import com.jinzi.web.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


@Data
@EqualsAndHashCode(callSuper = true)
public class ProductInfoSearchTextRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = -6337349622479990038L;

    private String searchText;
}
