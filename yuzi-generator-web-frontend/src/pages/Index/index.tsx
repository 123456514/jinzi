import {
  listGeneratorVoByPageSimplifyDataUsingPost, listGeneratorVoByPageUsingPost,
} from '@/services/backend/generatorController';
import {
  DownOutlined, LikeFilled,
  LikeOutlined, StarFilled,
  StarOutlined,
  UpOutlined
} from '@ant-design/icons';
import {PageContainer, ProFormSelect, ProFormText, QueryFilter} from '@ant-design/pro-components';
import '@umijs/max';
import { Link } from '@umijs/max';
import {Alert, Card, Flex, Input, List, message, Tabs, Tag, Typography} from 'antd';
import moment from 'moment';
import React, { useEffect, useState } from 'react';
import Marquee from 'react-fast-marquee';
import {doGeneratorFavourUsingPost} from "@/services/backend/generatorFavourController";
import {doThumbUsingPost} from "@/services/backend/generatorThumbController";
import Meta from "antd/es/card/Meta";

const DEFAULT_PAGE_PARAMS: PageRequest = {
  current: 1,
  pageSize: 4,
  sortField: 'createTime',
  // @ts-ignore
  sortOrder: 'newest',
};

const IndexPage: React.FC = () => {
  const [loading, setLoading] = useState<boolean>(true);
  const [dataList, setDataList] = useState<API.GeneratorVO[]>([]);
  const [total, setTotal] = useState<number>(0);
  const [showFilter, setShowFilter] = useState<boolean>(true);

  const [searchParams, setSearchParams] = useState<API.GeneratorQueryRequest>({
    ...DEFAULT_PAGE_PARAMS,
  });

  const doSearch = async () => {

    setLoading(true);
    try {
      // const res = await listGeneratorVoByPageUsingPost(searchParams);
      const res = await listGeneratorVoByPageSimplifyDataUsingPost(searchParams);
      setDataList(res.data?.records ?? []);
      setTotal(res.data?.total ?? 0);
    } catch (error: any) {
      message.error('获取数据失败！', error.message);
      setTimeout(() => {
        message.destroy(); // 关闭所有消息提示
      }, 3000);
    }
    setLoading(false);
  };

  useEffect(() => {
    doSearch();
  }, [searchParams]);

  const IconText = ({ icon, text, onClick }: { icon: any; text: string; onClick?: () => void }) => (
    <span onClick={onClick}>
      {React.createElement(icon, { style: { marginInlineEnd: 8 } })}
      {text}
    </span>
  );

  /**
   * 点赞
   * @param req
   */
  const doThumb = async (req: API.GeneratorThumbAddRequest) => {
    setLoading(true);
    try {
      const res = await doThumbUsingPost(req);
      if (res.code === 0) {
        message.success(res.data === 1 ? '点赞成功！' : '取消点赞！');
        setSearchParams({
          ...searchParams,
        });
      }
    } catch (error: any) {
      message.error('失败！', error.message);
    }
    setTimeout(() => {
      message.destroy(); // 关闭所有消息提示
    }, 3000);
    setLoading(false);
  };

  /**
   * 收藏
   * @param req
   */
  const doFavour = async (req: API.GeneratorFavourAddRequest) => {
    setLoading(true);
    try {
      const res = await doGeneratorFavourUsingPost(req);
      if (res.code === 0) {
        message.success(res.data === 1 ? '收藏成功！' : '取消收藏！');
        setSearchParams({
          ...searchParams,
        });
      }
    } catch (error: any) {
      message.error('失败！', error.message);
    }
    setTimeout(() => {
      message.destroy(); // 关闭所有消息提示
    }, 3000);
    setLoading(false);
  };

  /**
   * 展示标签列表
   * @param tags 标签列表
   */
  const tagListView = (tags?: string[]) => {
    if (!tags) {
      return <></>;
    }

    return (
      <div style={{ marginBottom: 8 }}>
        {tags.map((tag) => (
          <Tag key={tag}>{tag}</Tag>
        ))}
      </div>
    );
  };

  return (
    <PageContainer title={<></>}>
      <Alert
        banner
        type="success"
        message={
          <Marquee pauseOnHover gradient={false}>
            欢迎各位使用 金子代码生成 ，一起来制作出属于你的代码生成器吧！相信我，利用好这里，一定能够帮助你大幅提高开发效率！
          </Marquee>
        }></Alert>

      <div style={{ marginBottom: 16 }} />
      <Flex justify="center">
        <Input.Search
          placeholder="请输入想要查找的生成器"
          allowClear
          style={{ width: '40vw', minWidth: '233px' }}
          enterButton="搜索"
          size="large"
          onChange={(e) => {
            searchParams.searchText = e.target.value;
          }}
          onSearch={(value: string) => {
            setSearchParams({
              ...DEFAULT_PAGE_PARAMS,
              searchText: value,
            });
          }}
        />
      </Flex>
      <div style={{ marginBottom: 12 }}></div>
      <Tabs
        defaultActiveKey="newest"
        onChange={(e) => {
          if (e === 'newest') {
            setSearchParams({
              ...searchParams,
              sortOrder: e,
              sortField: 'createTime',
            });
          } else {
            setSearchParams({
              ...searchParams,
              sortOrder: e,
              sortField: 'thumbNum',
            });
          }
        }}
        tabBarExtraContent={
          <a
            style={{
              display: 'flex',
              gap: 4,
            }}
            onClick={() => {
              setShowFilter(!showFilter);
            }}
          >
            高级筛选 {showFilter ? <UpOutlined /> : <DownOutlined />}
          </a>
        }
        items={[
          {
            key: 'newest',
            label: '最新',
          },
          {
            key: 'recommend',
            label: '推荐',
          }
        ]}
      />
      {showFilter ? (
        <QueryFilter
          span={12}
          labelWidth="auto"
          split
          onFinish={async (values: API.GeneratorQueryRequest) => {
            setSearchParams({
              ...DEFAULT_PAGE_PARAMS,
              searchText: searchParams.searchText,
              ...values,
            });
          }}
        >
          <ProFormSelect label="标签" name="tags" mode="tags" />
          <ProFormText label="名称" name="name" />
          <ProFormText label="描述" name="description" />
        </QueryFilter>
      ) : null}

      <div style={{ marginBottom: 12 }}></div>
      <List<API.GeneratorVO>
        rowKey="id"
        loading={loading}
        grid={{
          gutter: 16,
          xs: 1,
          sm: 2,
          md: 3,
          lg: 3,
          xl: 4,
          xxl: 4,
        }}
        dataSource={dataList}
        pagination={{
          current: searchParams.current,
          pageSize: searchParams.pageSize,
          total: total,
          showSizeChanger: false,
          onChange(current: number, pageSize: number) {
            setSearchParams({
              ...searchParams,
              current,
              pageSize,
            });
          },
        }}
        renderItem={(data) => (
          <List.Item>
            <Card
              // style={{ width: 300 }}
              hoverable cover={<img alt={data.name} src={data.picture} />}
              actions={[
                    <IconText
                      // @ts-ignore
                      icon={data.hasFavour ? StarFilled : StarOutlined}
                      // @ts-ignore
                      text={data.favourNum}
                      key="list-vertical-star-o"
                      onClick={() => {
                        doFavour({
                          // @ts-ignore
                          generatorId: data.id,
                        });
                      }}
                    />,
                    <IconText
                      // @ts-ignore
                      icon={data.hasThumb ? LikeFilled : LikeOutlined}
                      // @ts-ignore
                      text={data.thumbNum}
                      key="list-vertical-like-o"
                      onClick={() => {
                        doThumb({
                          // @ts-ignore
                          generatorId: data.id,
                        });
                      }}
                    />,
                // eslint-disable-next-line react/jsx-key
                    <Typography.Paragraph type="secondary" style={{ fontSize: 12 }}>
                      {moment(data.updateTime).fromNow()}
                    </Typography.Paragraph>
              ]}>

              <Link to={`/generator/detail/${data.id}`}>
                <Meta
                  title={<a>{data.name}</a>}
                  description={
                    <Typography.Paragraph ellipsis={{ rows: 2}} style={{ "height": 44 }}>{data.description}</Typography.Paragraph>
                  }
                />
              </Link>
              {/* 展示标签 */}
              {tagListView(data.tags)}
            </Card>
          </List.Item>
        )}></List>

    </PageContainer>
  );
};

// @ts-ignore
export default IndexPage;
