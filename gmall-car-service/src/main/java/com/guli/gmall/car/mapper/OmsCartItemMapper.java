package com.guli.gmall.car.mapper;

import com.guli.gmall.bean.OmsCartItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OmsCartItemMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OmsCartItem record);

    int insertSelective(OmsCartItem record);

    OmsCartItem selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OmsCartItem record);

    int updateByPrimaryKey(OmsCartItem record);

    OmsCartItem selectCarExistByUser(@Param("memberId") String memberId, @Param("skuId") String skuId);

    List<OmsCartItem> selectByMemberId(String memberId);

    void updateBySkuId(OmsCartItem omsCartItem);
}