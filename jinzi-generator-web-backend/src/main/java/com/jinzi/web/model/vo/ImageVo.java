package com.jinzi.web.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 上传图片状态vo
 */
@Data
public class ImageVo implements Serializable {
    private static final long serialVersionUID = -4296258656223039373L;
    private String uid;
    private String name;
    private String status;
    private String url;
}