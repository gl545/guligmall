package com.guli.gmall.manager.mapper;

import com.guli.gmall.bean.PmsProductImage;

import java.util.List;

public interface PmsProductImageMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PmsProductImage record);

    int insertSelective(PmsProductImage record);

    PmsProductImage selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PmsProductImage record);

    int updateByPrimaryKey(PmsProductImage record);

    void deleteByProductId(Long id);

    List<PmsProductImage> selectBySpuId(String spuId);
}