package com.guli.gmall.manager.mapper;

import com.guli.gmall.bean.PmsSkuInfo;

import java.util.List;

public interface PmsSkuInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PmsSkuInfo record);

    int insertSelective(PmsSkuInfo record);

    PmsSkuInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PmsSkuInfo record);

    int updateByPrimaryKey(PmsSkuInfo record);

    List<PmsSkuInfo> selectSkuSaleAttrValueListBySpu(Long productId);

    List<PmsSkuInfo> selectSkuAll();
}