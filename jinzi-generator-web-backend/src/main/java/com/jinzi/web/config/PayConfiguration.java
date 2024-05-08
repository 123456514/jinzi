package com.jinzi.web.config;


import com.ijpay.alipay.AliPayApiConfig;
import com.ijpay.alipay.AliPayApiConfigKit;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
@AllArgsConstructor
public class PayConfiguration {

    @Resource
    private AliPayAccountConfig aliPayAccountConfig;
    @Bean
    public void aliPayApi() {
        AliPayApiConfig aliPayApiConfig = AliPayApiConfig.builder()
                .setAppId(aliPayAccountConfig.getAppId())
                .setAliPayPublicKey(aliPayAccountConfig.getAliPayPublicKey())
                .setCharset("UTF-8")
                .setPrivateKey(aliPayAccountConfig.getPrivateKey())
                .setServiceUrl("https://openapi-sandbox.dl.alipaydev.com/gateway.do")
                .build(); // 普通公钥方式
        AliPayApiConfigKit.setThreadLocalAliPayApiConfig(aliPayApiConfig);
    }
}