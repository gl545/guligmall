package com.guli.gmall.manager.mapper;

import com.guli.gmall.bean.PmsProductSaleAttr;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PmsProductSaleAttrMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PmsProductSaleAttr record);

    int insertSelective(PmsProductSaleAttr record);

    PmsProductSaleAttr selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PmsProductSaleAttr record);

    int updateByPrimaryKey(PmsProductSaleAttr record);

    void deleteByProductId(Long id);

    List<PmsProductSaleAttr> selectBySpuId(String spuId);

    List<PmsProductSaleAttr> selectSpuSaleAttrListCheckBySku(@Param("skuId") String skuId, @Param("productId") String productId);
}