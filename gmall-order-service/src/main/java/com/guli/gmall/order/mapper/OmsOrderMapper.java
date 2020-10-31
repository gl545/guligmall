package com.guli.gmall.order.mapper;

import com.guli.gmall.bean.OmsOrder;

public interface OmsOrderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OmsOrder record);

    int insertSelective(OmsOrder record);

    OmsOrder selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OmsOrder record);

    int updateByPrimaryKey(OmsOrder record);

    OmsOrder selectByOutTradeNo(String outTradeNo);

    void updateProcessStatus(OmsOrder omsOrder);
}