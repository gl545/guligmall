package com.guli.gmall.user.mapper;

import com.guli.gmall.bean.UmsMember;

import java.util.List;

public interface UmsMemberMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UmsMember record);

    int insertSelective(UmsMember record);

    UmsMember selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UmsMember record);

    int updateByPrimaryKey(UmsMember record);

    List<UmsMember> selectAll();

    List<UmsMember> selectUser(UmsMember umsMember);

    UmsMember selectUserByUid(String uid);
}