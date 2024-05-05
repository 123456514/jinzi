package com.jinzi.web.common;

import lombok.Data;

import java.io.Serializable;


@Data
public class IdRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;
}