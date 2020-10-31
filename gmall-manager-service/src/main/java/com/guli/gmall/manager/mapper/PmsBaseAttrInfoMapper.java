package com.guli.gmall.manager.mapper;

import com.guli.gmall.bean.PmsBaseAttrInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PmsBaseAttrInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PmsBaseAttrInfo record);

    int insertSelective(PmsBaseAttrInfo record);

    PmsBaseAttrInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PmsBaseAttrInfo record);

    int updateByPrimaryKey(PmsBaseAttrInfo record);

    List<PmsBaseAttrInfo> selectAttr(Integer catalog3Id);

    List<PmsBaseAttrInfo> selectAttrList(@Param("valueIdStr") String valueIdStr);
}