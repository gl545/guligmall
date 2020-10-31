package com.guli.gmall.manager.mapper;

import com.guli.gmall.bean.PmsSkuAttrValue;

import java.util.List;

public interface PmsSkuAttrValueMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PmsSkuAttrValue record);

    int insertSelective(PmsSkuAttrValue record);

    PmsSkuAttrValue selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PmsSkuAttrValue record);

    int updateByPrimaryKey(PmsSkuAttrValue record);

    List<PmsSkuAttrValue> selectBySkuId(Long skuId);
}