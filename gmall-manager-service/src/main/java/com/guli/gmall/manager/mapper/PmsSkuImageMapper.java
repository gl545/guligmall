package com.guli.gmall.manager.mapper;

import com.guli.gmall.bean.PmsSkuImage;

import java.util.List;

public interface PmsSkuImageMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PmsSkuImage record);

    int insertSelective(PmsSkuImage record);

    PmsSkuImage selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PmsSkuImage record);

    int updateByPrimaryKey(PmsSkuImage record);

    List<PmsSkuImage> selectBySkuId(String skuId);
}