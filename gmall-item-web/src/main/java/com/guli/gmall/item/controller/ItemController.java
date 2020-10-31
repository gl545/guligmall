package com.guli.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.guli.gmall.bean.PmsProductSaleAttr;
import com.guli.gmall.bean.PmsSkuInfo;
import com.guli.gmall.bean.PmsSkuSaleAttrValue;
import com.guli.gmall.service.SkuService;
import com.guli.gmall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@CrossOrigin
public class ItemController {

    @Reference(interfaceClass = SkuService.class,version = "1.0.0",check = false)
    private SkuService sKuService;

    @Reference(interfaceClass = SpuService.class,version = "1.0.0",check = false)
    private SpuService spuService;

    @RequestMapping("{skuId}.html")
    public String getSkuInfoBySkuId(@PathVariable String skuId, Model model){
        PmsSkuInfo pmsSkuInfo = sKuService.getSkuInfoBySkuId(skuId);
        model.addAttribute("skuInfo",pmsSkuInfo);
        List<PmsProductSaleAttr> pmsProductSaleAttrs = spuService.spuSaleAttrListCheckBySku(skuId,pmsSkuInfo.getProductId());
        model.addAttribute("spuSaleAttrListCheckBySku",pmsProductSaleAttrs);
        List<PmsSkuInfo> pmsSkuInfos = sKuService.getSkuSaleAttrValueListBySpu(pmsSkuInfo.getProductId());
        Map<String,String> map = new HashMap<String, String>();
        for (PmsSkuInfo skuInfo : pmsSkuInfos) {
            List<PmsSkuSaleAttrValue> pmsSkuSaleAttrValues = skuInfo.getSkuSaleAttrValueList();
            String key = "";
            for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : pmsSkuSaleAttrValues) {
                String skuSaleAttrValueId = String.valueOf(pmsSkuSaleAttrValue.getSaleAttrValueId());
                key += skuSaleAttrValueId + "|";
            }
            String value = String.valueOf(skuInfo.getId());
            map.put(key,value);
        }
        String skuAttrValueHashJsonStr = JSON.toJSONString(map);
        model.addAttribute("skuAttrValueHashJsonStr",skuAttrValueHashJsonStr);
        return "item";
    }
}
