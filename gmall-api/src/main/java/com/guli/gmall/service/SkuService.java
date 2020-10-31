package com.guli.gmall.service;

import com.guli.gmall.bean.PmsSkuInfo;

import java.util.List;

public interface SkuService {

    String saveSkuInfo(PmsSkuInfo pmsSkuInfo);

    PmsSkuInfo getSkuInfoBySkuId(String skuId);

    List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(Long productId);

    List<PmsSkuInfo> getSkuAll();
}
