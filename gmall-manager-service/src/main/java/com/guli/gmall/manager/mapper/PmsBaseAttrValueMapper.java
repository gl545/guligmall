package com.guli.gmall.manager.mapper;

import com.guli.gmall.bean.PmsBaseAttrValue;

import java.util.List;

public interface PmsBaseAttrValueMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PmsBaseAttrValue record);

    int insertSelective(PmsBaseAttrValue record);

    PmsBaseAttrValue selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PmsBaseAttrValue record);

    int updateByPrimaryKey(PmsBaseAttrValue record);

    List<PmsBaseAttrValue> selectByAttrId(String attrId);

    void delete(Long id);
}