package com.guli.gmall.manager.mapper;

import com.guli.gmall.bean.PmsBaseCatalog3;

import java.util.List;

public interface PmsBaseCatalog3Mapper {
    int deleteByPrimaryKey(Long id);

    int insert(PmsBaseCatalog3 record);

    int insertSelective(PmsBaseCatalog3 record);

    PmsBaseCatalog3 selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PmsBaseCatalog3 record);

    int updateByPrimaryKey(PmsBaseCatalog3 record);

    List<PmsBaseCatalog3> selectByCatalog2Id(Integer catalog2Id);
}