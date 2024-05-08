import {Card, message,Radio, Spin, Tooltip} from 'antd';
import React, {useEffect, useState} from 'react';
import {history} from '@umijs/max';
import ProCard from "@ant-design/pro-card";
import Alipay from "@/components/Icon/Alipay";
import {valueLength} from "@/pages/User/UserInfo";
import {useParams} from "@@/exports";
import {
  createOrderUsingPOST,
  getProductOrderByIdUsingGET,
  queryOrderStatusUsingPOST
} from "@/services/backend/orderController";

const PayOrder: React.FC = () => {
  const [loading, setLoading] = useState<boolean>(false);
  const [order, setOrder] = useState<API.ProductOrderVo>();
  const [, setTotal] = useState<any>("0.00");
  const [status, setStatus] = useState<string>('active');
  const [payType, setPayType] = useState<string>();
  const urlParams = new URL(window.location.href).searchParams;
  const codeUrl = urlParams.get("codeUrl")
  const urlPayType = urlParams.get("payType")
  const [, setQrCode] = useState<any>('暂未选择支付方式');
  const params = useParams()

  // 创建订单
  const createOrder = async () => {
    setLoading(true)
    setStatus("loading")
    // @ts-ignore
    const res = await createOrderUsingPOST({productId: params.id, payType: payType})
    if (res.code === 0 && res.data) {
      setOrder(res.data)
      // @ts-ignore
      setTotal((res.data.total) / 100)
      setStatus("active")
      setLoading(false)
      // setQrCode(res.data.codeUrl)
    }
    if (res.code === 50001) {
      history.back()
    }
  }



  const queryOrderStatus = async () => {
    const currentTime = new Date();
    const expirationTime = new Date(order?.expirationTime as any);
    if (currentTime > expirationTime) {
      setStatus("expired")
    }
    return await queryOrderStatusUsingPOST({orderNo: order?.orderNo})
  }

  const toAlipay = async () => {
    if (!params.id) {
      message.error('参数不存在');
      return;
    }
    setLoading(true)
    const res = await createOrderUsingPOST({productId: params.id, payType: "ALIPAY"})
    if (res.code === 0 && res.data) {
      message.loading("正在前往收银台,请稍后....")
      // window.open("http://localhost:8120/alipay/pay?productId=" + params.id)
      setTimeout(() => {
        document.write(res?.data?.formData as string);
        setLoading(false)
      }, 2000)
    } else {
      setLoading(false)
    }
  }
  const changePayType = (value: string) => {
    setPayType(value);
  };
  const getOrder = async () => {
    const res = await getProductOrderByIdUsingGET({id:params.id})
    console.log(res)
    if (res.code === 0 && res.data) {
      const data={
        productInfo:res.data,
        orderNo:res.data.orderNo,
        codeUrl:res.data.codeUrl
      }
      // @ts-ignore
      setOrder(data)
      // @ts-ignore
      setTotal((res.data.total))
      setStatus("active")
      setLoading(false)
    }
  }
  useEffect(() => {
    if (urlPayType) {
     setPayType(urlPayType)
     getOrder()
    }
  }, [])

  useEffect(() => {
    if (payType === "ALIPAY") {
      toAlipay()
    }
  }, [payType])

  useEffect(() => {
    if (order && order.orderNo && order.codeUrl) {
      const intervalId = setInterval(async () => {
        // 定时任务逻辑
        const res = await queryOrderStatus()
        if (res.data && res.code === 0) {
          setLoading(true)
          message.loading("支付成功,打款中....")
          clearInterval(intervalId);
          setTimeout(function () {
            setLoading(false)
            const urlParams = new URL(window.location.href).searchParams;
            history.push(urlParams.get('redirect') || '/account/center');
          }, 2000);
        } else {
          console.log("支付中...")
        }
      }, 3000);
      if (status === "expired") {
        clearInterval(intervalId);
      }
      return () => {
        clearInterval(intervalId);
      };
    }
  }, [order, status])

  useEffect(() => {
    if (!params.id) {
      message.error('参数不存在');
      return;
    }
    if (codeUrl) {
      setQrCode(codeUrl)
      setStatus("active")
      return;
    }
    if (!urlPayType && !payType) {
      setStatus("loading")
      return
    }
    if (urlPayType) {
      setPayType(urlPayType)
      return;
    }
    createOrder()
  }, [])




  return (
    <>
      <Card style={{minWidth: 385}}>
        <Spin spinning={loading}>
          <Card title={<strong>商品信息</strong>}>
            <div style={{marginLeft: 10}}>
              <h3>{order?.productInfo?.name}</h3>
              <h4>{valueLength(order?.productInfo?.description) ? order?.productInfo?.description : "暂无商品描述信息"}</h4>
            </div>
          </Card>
          <br/>
          <ProCard
            bordered
            headerBordered
            layout={"center"}
            title={<strong>支付方式</strong>}
          >
            <Radio.Group name="payType" value={payType}>
              <ProCard wrap gutter={18}>
                <ProCard
                  onClick={() => {
                    changePayType("ALIPAY")
                  }}
                  hoverable
                  style={{
                    margin: 10,
                    maxWidth: 260,
                    minWidth: 210,
                    border: payType === "ALIPAY" ? '1px solid #1890ff' : '1px solid rgba(128, 128, 128, 0.5)',
                  }}
                  colSpan={
                    {
                      xs: 24,
                      sm: 12,
                      md: 12,
                      lg: 12,
                      xl: 12
                    }
                  }
                >
                  <Radio value={"ALIPAY"} style={{fontSize: "1.2rem"}}>
                    <Alipay/> 支付宝
                  </Radio>
                </ProCard>
              </ProCard>
            </Radio.Group>
          </ProCard>
          <br/>
        </Spin>
      </Card>
    </>
  )
}

export default PayOrder;
