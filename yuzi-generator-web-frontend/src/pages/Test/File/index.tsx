import { COS_HOST } from '@/constants';
import {
  testDownloadFileUsingGet,
  testUploadFileUsingPost
} from '@/services/backend/fileController';
import { InboxOutlined } from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-components';
import { Button, Card, Divider, Flex, message, Upload, UploadProps } from 'antd';
import { saveAs } from 'file-saver';
import React, { useState } from 'react';

const { Dragger } = Upload;

const TestFilePage: React.FC = () => {
  const [value, setValue] = useState<string>(); // 用于保存信息

  const props: UploadProps = {
    name: 'file',
    multiple: false,// 表示的是 文件只能上传一份
    maxCount: 1, // 限制文件的上传最大个数
    // 创建一个异步自定义请求
    customRequest: async (fileObj: any) => {
      try {
        // 要使用service 中的链接前后端数据方法的时候一定要带上 await 或者带上 then()
        const res = await testUploadFileUsingPost({},fileObj.file);
        fileObj.onSuccess(res.data);
        setValue(res.data);
      } catch (e: any) {
        message.error('上传失败' + e.message);
        fileObj.onError(e);
      }
    },
    onRemove: () => {
      setValue(undefined);
    },
  };
  return (
    <PageContainer>
      <Flex gap={16}>
        <Card title="文件上传">
          <Dragger {...props}>
            <p className="ant-upload-drag-icon">
              <InboxOutlined />
            </p>
            <p className="ant-upload-text">Click or drag file to this area to upload</p>
            <p className="ant-upload-hint">
              Support for a single or bulk upload. Strictly prohibit from uploading company data or
              other band files
            </p>
          </Dragger>
        </Card>
        <Card title="文件下载">
          文件地址: {COS_HOST + value}
          <Divider />
          <img src={COS_HOST + value} alt="" height={200} />
          <Divider />
          <Button
            onClick={async () => {
              const blob = await testDownloadFileUsingGet(
                {
                  filepath: value,
                },
                {
                  responseType: 'blob',
                },
              );
              const fullPath = COS_HOST + value;
              saveAs(blob, fullPath.substring(fullPath.lastIndexOf('/') + 1));
            }}
          >
            点击下载文件
          </Button>
        </Card>
      </Flex>
    </PageContainer>
  );
};
export default TestFilePage;
