package com.guli.gmall.manager.mapper;

import com.guli.gmall.bean.PmsBaseCatalog2;

import java.util.List;

public interface PmsBaseCatalog2Mapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PmsBaseCatalog2 record);

    int insertSelective(PmsBaseCatalog2 record);

    PmsBaseCatalog2 selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PmsBaseCatalog2 record);

    int updateByPrimaryKey(PmsBaseCatalog2 record);

    List<PmsBaseCatalog2> selectByCatalog1Id(Integer catalog1Id);
}