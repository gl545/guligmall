package com.guli.gmall.service;

import com.guli.gmall.bean.PmsBaseSaleAttr;
import com.guli.gmall.bean.PmsProductImage;
import com.guli.gmall.bean.PmsProductInfo;
import com.guli.gmall.bean.PmsProductSaleAttr;

import java.util.List;

public interface SpuService {
    List<PmsProductInfo> spuList(String catalog3Id);

    String saveSpuInfo(PmsProductInfo pmsProductInfo);

    List<PmsBaseSaleAttr> baseSaleAttrList();

    List<PmsProductImage> spuImageList(String spuId);

    List<PmsProductSaleAttr> spuSaleAttrList(String spuId);

    List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String skuId, Long productId);
}
