import {useModel} from '@umijs/max';
import {Card, theme, Typography} from 'antd';
import React, {useEffect, useState} from 'react';
import {useParams} from "@@/exports";
import GetGiftModal from "@/components/Gift/GetGift";
import {getUserByInvitationCodeUsingPOST} from "@/services/backend/userController";


const {Text, Title} = Typography;
/**
 * 每个单独的卡片，为了复用样式抽成了组件
 * @param param0
 * @returns
 */
const InfoCard: React.FC<{
  title: any
  index: number;
  desc: any;
  href: string;
}> = ({title, index, desc}) => {
  const {useToken} = theme;
  const {token} = useToken();
  return (
    <div
      style={{
        backgroundColor: token.colorBgContainer,
        boxShadow: token.boxShadow,
        borderRadius: '8px',
        fontSize: '14px',
        color: token.colorTextSecondary,
        lineHeight: '22px',
        padding: '16px 19px',
        minWidth: '220px',
        flex: 1,
      }}
    >
      <div
        style={{
          display: 'flex',
          gap: '4px',
          alignItems: 'center',
        }}
      >
        <div
          style={{
            width: 48,
            height: 48,
            lineHeight: '22px',
            backgroundSize: '100%',
            textAlign: 'center',
            padding: '8px 16px 16px 12px',
            color: '#FFF',
            fontWeight: 'bold',
            backgroundImage:
              "url('https://gw.alipayobjects.com/zos/bmw-prod/daaf8d50-8e6d-4251-905d-676a24ddfa12.svg')",
          }}
        >
          {index}
        </div>
        <div
          style={{
            fontSize: '16px',
            color: token.colorText,
            paddingBottom: 8,
          }}
        >
          {title}
        </div>
      </div>
      <div
        style={{
          fontSize: '14px',
          color: token.colorTextSecondary,
          textAlign: 'justify',
          lineHeight: '22px',
          marginBottom: 8,
        }}
      >
        {desc}
      </div>
      <br/>
    </div>
  );
};


const Welcome: React.FC = () => {
  const {token} = theme.useToken();
  const {initialState} = useModel('@@initialState');
  const [open, setOpen] = useState(false);
  const [data, setData] = useState<API.UserVO>()
  const params = useParams()
  const getUserByInvitationCode = async () => {
    const res = await getUserByInvitationCodeUsingPOST({invitationCode: params.id})
    if (res.code === 0 && res.data) {
      if (initialState?.loginUser && initialState?.loginUser.invitationCode === params.id) {
        // message.error("不能邀请自己")
        return
      }
      if (!initialState?.loginUser) {
        setOpen(true)
        setData(res.data)
      }
    }
  }
  useEffect(() => {
    if (params.id) {
      getUserByInvitationCode()
    }
  }, [])

  return (
    <>

      <Card
        style={{
          borderRadius: 8,
        }}
        bodyStyle={{
          backgroundImage:
            initialState?.settings?.navTheme === 'realDark'
              ? 'background-image: linear-gradient(75deg, #1A1B1F 0%, #191C1F 100%)'
              : 'background-image: linear-gradient(75deg, #FBFDFF 0%, #F5F7FF 100%)',
        }}
      >
        <div
          style={{
            backgroundPosition: '100% -30%',
            backgroundRepeat: 'no-repeat',
            backgroundSize: '274px auto',
            backgroundImage:
              "url('https://gw.alipayobjects.com/mdn/rms_a9745b/afts/img/A*BuFmQqsB2iAAAAAAAAAAAAAAARQnAQ')",
          }}
        >
          <div
            style={{
              fontSize: '20px',
              color: token.colorTextHeading,
            }}
          >
            <Title level={3}> 欢迎使用 金子代码生成 服务平台 🎉</Title>
          </div>
          <div
            style={{
              fontSize: '14px',
              color: token.colorTextSecondary,
              lineHeight: '22px',
              marginTop: 16,
              marginBottom: 32,
              width: '100%',
            }}
          >
            <Text strong>
              <Title level={4}>金子 代码生成平台是一个为开发者提高工作效率，简化繁杂的日常开发的服务平台 🛠</Title>
              <Title level={5}>
                😀 作为用户您可以通过注册登录账户，获取生成器使用和制作权限，可以使用他人的代码生成器快速生成自己想要的代码，同时可以通过简单的配置制作自己的代码生成器，简化在工作中经常要编写的登录，注册等功能开发。
                <br/>
                💻 在团队开发中，要生成的代码可能需要频繁拜年话和持续更新维护的，如果有一个线上平台来维护多个不同的代码生成器。支持在线编辑和共享生成器，在提高开发效率的同时，将有利于协作共建，打造高质量的代码生成器。
                <br/>
                🤝 虽然网上有很多代码生成器，但是都是别人制作封装好的，很多时候无法满足实际开发的定制化需求（比如要在每个类上增加特定的注解和注释）。这也是为什么明明有代码生成器，很多开发者还是会抱怨自己的工作总是复制粘贴，编写重复的代码，天天CRUD（增删查改），如果能够有一个工具帮助开发者快速定制属于自己的代码生成器，那么将进一步提高开发效率。
                <br/>
                👌 我们还提供了<a href={"https://doc.qimuu.icu"} target={"_blank"} rel="noreferrer">本项目的源码</a>和技术支持，欢迎大家在Github为服务平台点击Star。
                <br/>
              </Title>
            </Text>
          </div>
          <div
            style={{
              display: 'flex',
              flexWrap: 'wrap',
              gap: 16,
            }}
          >
            <InfoCard
              index={1}
              href="https://api.qimuu.icu/"
              title={<Title level={5}>使用代码生成器</Title>}
              desc={<Text
                strong>使用别人的生成器，简化自己的本地开发</Text>}
            />
            <InfoCard
              index={2}
              href="https://api.qimuu.icu/"
              title={<Title level={5}>发表自己的代码生成器</Title>}
              desc={<Text
                strong>通过简单的配置制作自己的代码生成器，可以共享代码平台上，可以和他人协作开发代码生成器。
              </Text>}
            />
            <InfoCard
              index={3}
              href="https://api.qimuu.icu/"
              title={<Title level={5}>项目源码和技术支持</Title>}
              desc={<Text
                strong>平台提供了详细的项目源码和技术支持，帮助开发者快速开发，解决遇到的问题和困难。</Text>}
            />
            <InfoCard
              index={4}
              href="https://api.qimuu.icu/"
              title={<Title level={5}>简化开发，便捷工作</Title>}
              desc={<Text
                strong>平台致力于提供简化开发和便捷工作，采用了安全措施和技术手段，保障用户数据的安全性,隐私保护,快速开发。</Text>}
            />
          </div>
        </div>
        <GetGiftModal data={data} onCancel={() => setOpen(false)} open={open}/>
      </Card>

    </>
  );
};

export default Welcome;
