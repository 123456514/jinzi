package com.jinzi.web.service.impl;

import com.jinzi.web.service.MailService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class MailServiceImplTest {
    @Resource
    private MailService mailServiceImpl;

    @Test
    void sendMail() {
        mailServiceImpl.sendMail("2670253693@qq.com", "测试", "测试");
    }
}