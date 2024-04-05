import { GithubOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import '@umijs/max';
import React from 'react';

const Footer: React.FC = () => {
  const defaultMessage = '测试开发工程师周津';
  const currentYear = new Date().getFullYear();
  return (
    <DefaultFooter
      style={{
        background: 'none',
      }}
      copyright={`${currentYear} ${defaultMessage}`}
      links={[
        {
          key: 'codeNav',
          title: '我的编程知识圈子',
          href: 'https://yupi.icu',
          blankTarget: true,
        },
        {
          key: 'Ant Design',
          title: '编程宝典',
          href: 'https://codefather.cn',
          blankTarget: true,
        },
        {
          key: 'github',
          title: (
            <>
              <GithubOutlined /> 金子源码
            </>
          ),
          href: 'https://github.com/liyupi',
          blankTarget: true,
        },
      ]}
    />
  );
};
export default Footer;
