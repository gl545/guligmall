package com.guli.gmall.manager.mapper;

import com.guli.gmall.bean.PmsBaseCatalog1;

import java.util.List;

public interface PmsBaseCatalog1Mapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PmsBaseCatalog1 record);

    int insertSelective(PmsBaseCatalog1 record);

    PmsBaseCatalog1 selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PmsBaseCatalog1 record);

    int updateByPrimaryKey(PmsBaseCatalog1 record);

    List<PmsBaseCatalog1> selectAll();
}