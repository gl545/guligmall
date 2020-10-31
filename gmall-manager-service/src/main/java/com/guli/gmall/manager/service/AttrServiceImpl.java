package com.guli.gmall.manager.service;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.guli.gmall.bean.PmsBaseAttrInfo;
import com.guli.gmall.bean.PmsBaseAttrValue;
import com.guli.gmall.manager.mapper.PmsBaseAttrInfoMapper;
import com.guli.gmall.manager.mapper.PmsBaseAttrValueMapper;
import com.guli.gmall.service.AttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@Service(interfaceClass = AttrService.class,version = "1.0.0",timeout = 15000)
public class AttrServiceImpl implements AttrService {


    @Autowired
    private PmsBaseAttrInfoMapper pmsBaseAttrInfoMapper;

    @Autowired
    private PmsBaseAttrValueMapper pmsBaseAttrValueMapper;

    @Override
    public List<PmsBaseAttrInfo> getAttrList(Set<String> set) {
        String valueIdStr = StringUtils.join(set, ",");
        //System.out.println(valueIdStr);
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = pmsBaseAttrInfoMapper.selectAttrList(valueIdStr);
        return pmsBaseAttrInfos;
    }

    @Override
    public List<PmsBaseAttrValue> getAttrValueList(String attrId) {
        List<PmsBaseAttrValue> pmsBaseAttrValues = pmsBaseAttrValueMapper.selectByAttrId(attrId);
        return pmsBaseAttrValues;
    }

    @Override
    public String saveAttr(PmsBaseAttrInfo pmsBaseAttrInfo) {
        Long id = pmsBaseAttrInfo.getId();
        List<PmsBaseAttrValue> pmsBaseAttrValues = pmsBaseAttrInfo.getAttrValueList();
        if(id == null){
            pmsBaseAttrInfoMapper.insert(pmsBaseAttrInfo);
            for (PmsBaseAttrValue pmsBaseAttrValue : pmsBaseAttrValues) {
                pmsBaseAttrValue.setAttrId(pmsBaseAttrInfo.getId());
                pmsBaseAttrValueMapper.insert(pmsBaseAttrValue);
            }
        }else{
            pmsBaseAttrInfoMapper.updateByPrimaryKeySelective(pmsBaseAttrInfo);
            pmsBaseAttrValueMapper.delete(id);
            for (PmsBaseAttrValue pmsBaseAttrValue : pmsBaseAttrValues) {
                pmsBaseAttrValue.setAttrId(pmsBaseAttrInfo.getId());
                pmsBaseAttrValueMapper.insert(pmsBaseAttrValue);

            }
        }

        return "success";
    }

    @Override
    public List<PmsBaseAttrInfo> selectAttr(Integer catalog3Id) {

        List<PmsBaseAttrInfo> pmsBaseAttrInfos = pmsBaseAttrInfoMapper.selectAttr(catalog3Id);
        for (PmsBaseAttrInfo pmsBaseAttrInfo : pmsBaseAttrInfos) {
            Long attrId = pmsBaseAttrInfo.getId();
            List<PmsBaseAttrValue> pmsBaseAttrValues = pmsBaseAttrValueMapper.selectByAttrId(String.valueOf(attrId));
            pmsBaseAttrInfo.setAttrValueList(pmsBaseAttrValues);
        }
        return pmsBaseAttrInfos;
    }
}
