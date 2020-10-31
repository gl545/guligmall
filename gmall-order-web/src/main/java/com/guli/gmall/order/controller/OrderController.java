package com.guli.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.guli.gmall.annocation.LoginRequire;
import com.guli.gmall.bean.OmsCartItem;
import com.guli.gmall.bean.OmsOrder;
import com.guli.gmall.bean.OmsOrderItem;
import com.guli.gmall.bean.UmsMemberReceiveAddress;
import com.guli.gmall.service.CarService;
import com.guli.gmall.service.OrderService;
import com.guli.gmall.service.UmsMemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
public class OrderController {

    @Reference(interfaceClass = UmsMemberService.class,version = "1.0.0",check = false)
    private UmsMemberService umsMemberService;

    @Reference(interfaceClass = CarService.class,version = "1.0.0",check = false)
    private CarService carService;

    @Reference(interfaceClass = OrderService.class,version = "1.0.0",check = false)
    private OrderService orderService;

    @LoginRequire(loginSuccess = true)
    @RequestMapping("submitOrder")
    public ModelAndView submitOrder(String userAddressId, BigDecimal totalAmount, String tradeCode, HttpServletRequest request, HttpServletResponse response, ModelMap modelMap){
        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");
        ModelAndView mv= new ModelAndView();

        //检查交易码
        boolean flag =orderService.checkTradeCode(memberId,tradeCode);
        if(!flag){
            mv.setViewName("tradeFail");
            return mv;
        }
        //生成订单编号
        String outTradeNo = "gmall";
        outTradeNo += System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYYMMDDHHmmss");
        outTradeNo += simpleDateFormat.format(new Date());
        List<OmsOrderItem> omsOrderItems = new ArrayList<>();
        //通过用户Id查购物车数据
        List<OmsCartItem> omsCartItems = carService.getCarList(memberId);
        for (OmsCartItem omsCartItem : omsCartItems) {
            if("1".equals(omsCartItem.getIsChecked())){
                OmsOrderItem omsOrderItem = new OmsOrderItem();
                //查价格
                boolean b = orderService.checkPrice(omsCartItem.getProductSkuId(),omsCartItem.getPrice());
                if(!b){
                    mv.setViewName("tradeFail");
                    return mv;
                }
                omsOrderItem.setProductQuantity(omsCartItem.getQuantity());
                omsOrderItem.setProductName(omsCartItem.getProductName());
                omsOrderItem.setProductPic(omsCartItem.getProductPic());
                omsOrderItem.setProductPrice(omsCartItem.getPrice());
                omsOrderItem.setProductCategoryId(omsCartItem.getProductCategoryId());
                omsOrderItem.setProductSkuId(omsCartItem.getProductSkuId());
                omsOrderItem.setOrderSn(outTradeNo);
                omsOrderItem.setRealAmount(omsCartItem.getTotalPrice());
                omsOrderItem.setProductId(omsCartItem.getProductId());
                omsOrderItem.setProductSkuCode("111111111");

                omsOrderItems.add(omsOrderItem);
            }
        }
        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setOmsOrderItems(omsOrderItems);
        omsOrder.setTotalAmount(totalAmount);
        omsOrder.setStatus(3);
        omsOrder.setSourceType(0);
        omsOrder.setOrderType(1);
        omsOrder.setPayType(1);
        omsOrder.setNote("快点发货");
        omsOrder.setPayAmount(totalAmount);
        omsOrder.setOrderSn(outTradeNo);
        omsOrder.setMemberUsername(nickname);
        omsOrder.setMemberId(Long.valueOf(memberId));
        omsOrder.setCreateTime(new Date());
        omsOrder.setAutoConfirmDay(7);
        UmsMemberReceiveAddress umsMemberReceiveAddress = umsMemberService.getReceiveAddressById(userAddressId);
        //当前日期加一天
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,1);
        Date time = calendar.getTime();
        omsOrder.setReceiveTime(time);
        omsOrder.setReceiverRegion(umsMemberReceiveAddress.getRegion());
        omsOrder.setReceiverProvince(umsMemberReceiveAddress.getProvince());
        omsOrder.setReceiverPostCode(umsMemberReceiveAddress.getPostCode());
        omsOrder.setReceiverPhone(umsMemberReceiveAddress.getPhoneNumber());
        omsOrder.setReceiverName(umsMemberReceiveAddress.getName());
        omsOrder.setReceiverDetailAddress(umsMemberReceiveAddress.getDetailAddress());
        omsOrder.setReceiverCity(umsMemberReceiveAddress.getCity());

        //查库存
        //生成订单,删除购物车相应数据
        orderService.saveOrder(omsOrder);
        //重定向到支付页面
        mv.setViewName("redirect:http://127.0.0.1:8087/index");
        mv.addObject("outTradeNo",outTradeNo);
        mv.addObject("totalAmount",totalAmount);
        return mv;
    }


    @LoginRequire(loginSuccess = true)
    @RequestMapping("toTrade")
    public String toTrade(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap){
        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberService.getUserAddrList(memberId);
        modelMap.put("userAddressList",umsMemberReceiveAddresses);
        List<OmsCartItem> cartItems = carService.getCarList(memberId);

        List<OmsOrderItem> omsOrderItems = new ArrayList<>();
        BigDecimal totalAmount = new BigDecimal("0");
        for (OmsCartItem cartItem : cartItems) {
            if("1".equals(cartItem.getIsChecked())){
                OmsOrderItem omsOrderItem = new OmsOrderItem();
                omsOrderItem.setProductName(cartItem.getProductName());
                omsOrderItem.setProductPic(cartItem.getProductPic());
                omsOrderItem.setProductPrice(cartItem.getPrice());
                omsOrderItem.setProductQuantity(cartItem.getQuantity());
                totalAmount = totalAmount.add(cartItem.getTotalPrice());
                omsOrderItems.add(omsOrderItem);
            }
        }
        modelMap.put("orderDetailList",omsOrderItems);
        modelMap.put("totalAmount",totalAmount);
        String tradeCode = orderService.getTradeCode(memberId);
        modelMap.put("tradeCode",tradeCode);

        return "trade";
    }
}
