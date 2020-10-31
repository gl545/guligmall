package com.guli.gmall.payment.mapper;

import com.guli.gmall.bean.PaymentInfo;

public interface PaymentInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PaymentInfo record);

    int insertSelective(PaymentInfo record);

    PaymentInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PaymentInfo record);

    int updateByPrimaryKey(PaymentInfo record);

    void updateByOutTradeNo(PaymentInfo paymentInfo);
}