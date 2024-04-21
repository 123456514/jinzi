package com.jinzi.web.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 用户取消绑定电子邮件请求
 */
@Data
public class UserUnBindEmailRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String emailAccount;

    private String captcha;
}
