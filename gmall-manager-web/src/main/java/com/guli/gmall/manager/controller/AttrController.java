package com.guli.gmall.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.guli.gmall.bean.PmsBaseAttrInfo;
import com.guli.gmall.bean.PmsBaseAttrValue;
import com.guli.gmall.service.AttrService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@CrossOrigin
public class AttrController {

    @Reference(interfaceClass = AttrService.class,version = "1.0.0",check = false)
    private AttrService attrService;


    @RequestMapping("/getAttrValueList")
    @ResponseBody
    public List<PmsBaseAttrValue> getAttrValueList(String attrId){
        List<PmsBaseAttrValue> pmsBaseAttrValues  = attrService.getAttrValueList(attrId);
        return pmsBaseAttrValues;
    }

    @RequestMapping(value = "/saveAttrInfo")
    @ResponseBody
    public String saveAttrInfo(@RequestBody PmsBaseAttrInfo pmsBaseAttrInfo){
        String success = attrService.saveAttr(pmsBaseAttrInfo);
        return success;
    }

    @RequestMapping(value = "/attrInfoList")
    @ResponseBody
    public List<PmsBaseAttrInfo> attrInfoList(Integer catalog3Id){
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = attrService.selectAttr(catalog3Id);
        return pmsBaseAttrInfos;
    }
}
