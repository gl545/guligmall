package com.guli.gmall.service;

import com.guli.gmall.bean.PmsSearchSkuInfo;
import com.guli.gmall.bean.SkuSearchParam;

import java.util.List;

public interface SearchService {

    void put();

    List<PmsSearchSkuInfo> getSkuId(SkuSearchParam skuSearchParam);
}
