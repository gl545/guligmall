package com.guli.gmall.service;

import com.guli.gmall.bean.PmsBaseCatalog1;
import com.guli.gmall.bean.PmsBaseCatalog2;
import com.guli.gmall.bean.PmsBaseCatalog3;

import java.util.List;

public interface PmsBaseCatalogService {
    List<PmsBaseCatalog1> getAllPmsBaseCatalog1();

    List<PmsBaseCatalog2> getPmsBaseCatalog2(Integer catalog1Id);

    List<PmsBaseCatalog3> getPmsBaseCatalog3(Integer catalog2Id);
}
