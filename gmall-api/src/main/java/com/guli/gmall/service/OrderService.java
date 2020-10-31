package com.guli.gmall.service;

import com.guli.gmall.bean.OmsOrder;

import java.math.BigDecimal;

public interface OrderService {
    String getTradeCode(String memberId);

    boolean checkTradeCode(String memberId, String tradeCode);

    boolean checkPrice(Long productSkuId, BigDecimal price);

    void saveOrder(OmsOrder omsOrder);

    OmsOrder getOrderInfoByOutTradeNo(String outTradeNo);

    void updateProcessStatus(OmsOrder omsOrder);
}
