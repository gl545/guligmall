package com.guli.gmall.manager.mapper;

import com.guli.gmall.bean.PmsProductSaleAttrValue;

import java.util.List;

public interface PmsProductSaleAttrValueMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PmsProductSaleAttrValue record);

    int insertSelective(PmsProductSaleAttrValue record);

    PmsProductSaleAttrValue selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PmsProductSaleAttrValue record);

    int updateByPrimaryKey(PmsProductSaleAttrValue record);

    void deleteByProductId(Long id);

    List<PmsProductSaleAttrValue> select(PmsProductSaleAttrValue pmsProductSaleAttrValue);
}