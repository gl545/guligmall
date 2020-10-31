package com.guli.gmall.service;

import com.guli.gmall.bean.PmsBaseAttrInfo;
import com.guli.gmall.bean.PmsBaseAttrValue;

import java.util.List;
import java.util.Set;

public interface AttrService {
    List<PmsBaseAttrInfo> selectAttr(Integer catalog3Id);

    String saveAttr(PmsBaseAttrInfo pmsBaseAttrInfo);

    List<PmsBaseAttrValue> getAttrValueList(String attrId);

    List<PmsBaseAttrInfo> getAttrList(Set<String> set);
}
