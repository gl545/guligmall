package com.guli.gmall.manager.mapper;

import com.guli.gmall.bean.PmsProductInfo;

import java.util.List;

public interface PmsProductInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PmsProductInfo record);

    int insertSelective(PmsProductInfo record);

    PmsProductInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PmsProductInfo record);

    int updateByPrimaryKey(PmsProductInfo record);

    List<PmsProductInfo> select(String catalog3);
}