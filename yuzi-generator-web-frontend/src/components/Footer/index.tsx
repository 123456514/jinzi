import {GithubOutlined, WechatOutlined} from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import '@umijs/max';
import React from 'react';
import {Tooltip} from "antd";
import wechat from "@/assets/WeChat.png"
const Footer: React.FC = () => {
  const defaultMessage = '测试开发工程师 周津';
  const currentYear = new Date().getFullYear();
  return (
    <DefaultFooter
      style={{
        background: 'none',
      }}
      copyright={`${currentYear} ${defaultMessage}`}
      links={[
        {
          key: 'github',
          title: (
            <Tooltip title="查看本站技术及源码，欢迎 star">
              <GithubOutlined/> 支持项目
            </Tooltip>
          ),
          href: 'https://github.com/123456514/jinzi',
          blankTarget: true,
        },
        {
          key: 'contact',
          title: (
            <Tooltip title={<img src={wechat} alt="微信 code_nav" width="120"/>}>
              <WechatOutlined/> 联系作者
            </Tooltip>
          ),
          href: 'https://img.qimuu.icu/typory/WeChat.jpg',
          blankTarget: true,
        },
      ]}
    />
  );
};
export default Footer;
