package com.guli.gmall.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.guli.gmall.bean.PmsSkuInfo;
import com.guli.gmall.service.SkuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin
public class SkuController {

    @Reference(interfaceClass = SkuService.class,version = "1.0.0",check = false)
    private SkuService skuService;

    @RequestMapping("saveSkuInfo")
    @ResponseBody
    public String saveSkuInfo(@RequestBody PmsSkuInfo pmsSkuInfo){
        pmsSkuInfo.setProductId(pmsSkuInfo.getSpuId());
        if(pmsSkuInfo.getSkuDefaultImg() == null){
            pmsSkuInfo.setSkuDefaultImg(pmsSkuInfo.getSkuImageList().get(0).getImgUrl());
        }
        String success = skuService.saveSkuInfo(pmsSkuInfo);
        return success;
    }





}
