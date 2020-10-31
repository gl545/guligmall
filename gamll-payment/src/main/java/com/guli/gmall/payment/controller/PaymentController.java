package com.guli.gmall.payment.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.guli.gmall.annocation.LoginRequire;
import com.guli.gmall.bean.OmsOrder;
import com.guli.gmall.bean.PaymentInfo;
import com.guli.gmall.payment.config.AlipayConfig;
import com.guli.gmall.service.OrderService;
import com.guli.gmall.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PaymentController {

    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private PaymentService paymentService;

    @Reference(interfaceClass = OrderService.class,version = "1.0.0",check = false)
    private OrderService orderService;

    @RequestMapping("/alipay/callback/return")
    @LoginRequire(loginSuccess = true)
    public String callback(HttpServletRequest request, ModelMap modelMap) {

        //http://127.0.0.1:8087/alipay/callback/return?charset=utf-8&out_trade_no=gmall1600352185096202009261221625&method=alipay.trade.page.pay.return&total_amount=0.01&sign=jMxXSrK606P7s8Ts3aezGkkC7Lc3BpIfxTfZCo0Cj6Uwkgr4sQqcDSFe4bXndMYDa1YLAY%2B2RQdLJQCP1i7S3w7geQQ%2Fl%2FJqyqXGOqyNfZtM%2FwA0qfghEAFtSzWjzrKx5ouCFy88cir82mhEeQ%2BNTozW0TSHViZ15%2BQrwCLY085EENjCM67riuDHcus%2FtbS6bZFDEh1UIscE7WHsbL8%2B0wgaXxqlCL%2BBh23yAsaoa2c6q%2FOfv%2FnxJ%2BCs9wNQhSLkUgibHG72UV4CCVG8s3AzeopCIr3W1eKkYKJwsguuKo24wMWgHcqlwADoelRlorWVRVws2ciFmdWpg9Zg1WrOEw%3D%3D&trade_no=2020091722001424360500941628&auth_app_id=2021000116682318&version=1.0&app_id=2021000116682318&sign_type=RSA2&seller_id=2088621954967832&timestamp=2020-09-17+22%3A16%3A55
        String outTradeNo = request.getParameter("out_trade_no");
        String sign = request.getParameter("sign");
        String tradeNo = request.getParameter("trade_no");
        String subject = request.getParameter("subject");
        String totalAmount = request.getParameter("total_amount");
        String callBackContent = request.getQueryString();
        //更新用户支付数据
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOrderSn(outTradeNo);
        paymentInfo.setPaymentStatus("已支付");
        paymentInfo.setAlipayTradeNo(tradeNo);
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setCallbackContent(callBackContent);
        paymentService.updatePayment(paymentInfo);
        return "finish";
    }

    @RequestMapping("/mx/submit")
    @LoginRequire(loginSuccess = true)
    public String mx(String outTradeNo, BigDecimal totalAmount, HttpServletRequest request, ModelMap modelMap) {

        return null;
    }

    @RequestMapping("/alipay/submit")
    @LoginRequire(loginSuccess = true)
    @ResponseBody
    public String alipay(String outTradeNo, BigDecimal totalAmount, HttpServletRequest request, ModelMap modelMap) {
        AlipayTradePagePayRequest alipayRequest=new AlipayTradePagePayRequest();

        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);
        Map<String,Object> map = new HashMap<>();
        map.put("out_trade_no",outTradeNo);
        map.put("product_code","FAST_INSTANT_TRADE_PAY");
        map.put("total_amount",0.01);
        map.put("subject","一加7T");
        String parm = JSON.toJSONString(map);
        alipayRequest.setBizContent(parm);
        String form = null;
        try {
            form = alipayClient.pageExecute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        OmsOrder omsOrder = orderService.getOrderInfoByOutTradeNo(outTradeNo);
        //添加用户支付信息
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOrderSn(outTradeNo);
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setTotalAmount(totalAmount);
        paymentInfo.setPaymentStatus("未支付");
        paymentInfo.setSubject("谷粒商城商品一件");
        paymentInfo.setOrderId(String.valueOf(omsOrder.getId()));
        paymentService.savePaymentInfo(paymentInfo);
        return form;
    }
    @RequestMapping("/index")
    @LoginRequire(loginSuccess = true)
    public String index(String outTradeNo, BigDecimal totalAmount, HttpServletRequest request, ModelMap modelMap){
        String memberId = (String) request.getAttribute("memberId");
        String nickName = (String) request.getAttribute("nickname");
        modelMap.put("outTradeNo",outTradeNo);
        modelMap.put("totalAmount",totalAmount);
        modelMap.put("nickName",nickName);
        return "index";
    }
}
