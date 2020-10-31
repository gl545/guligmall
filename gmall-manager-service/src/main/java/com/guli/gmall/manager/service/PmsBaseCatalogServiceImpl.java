package com.guli.gmall.manager.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.guli.gmall.manager.mapper.PmsBaseCatalog1Mapper;
import com.guli.gmall.manager.mapper.PmsBaseCatalog2Mapper;
import com.guli.gmall.manager.mapper.PmsBaseCatalog3Mapper;
import com.guli.gmall.bean.PmsBaseCatalog1;
import com.guli.gmall.bean.PmsBaseCatalog2;
import com.guli.gmall.bean.PmsBaseCatalog3;
import com.guli.gmall.service.PmsBaseCatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Service(interfaceClass = PmsBaseCatalogService.class,version = "1.0.0",timeout = 15000)
public class PmsBaseCatalogServiceImpl implements PmsBaseCatalogService {


    @Autowired
    private PmsBaseCatalog1Mapper pmsBaseCatalog1Mapper;
    @Autowired
    private PmsBaseCatalog2Mapper pmsBaseCatalog2Mapper;

    @Autowired
    private PmsBaseCatalog3Mapper pmsBaseCatalog3Mapper;

    @Override
    public List<PmsBaseCatalog3> getPmsBaseCatalog3(Integer catalog2Id) {
        List<PmsBaseCatalog3> pmsBaseCatalog3s = pmsBaseCatalog3Mapper.selectByCatalog2Id(catalog2Id);
        return pmsBaseCatalog3s;
    }

    @Override
    public List<PmsBaseCatalog2> getPmsBaseCatalog2(Integer catalog1Id) {
        List<PmsBaseCatalog2> pmsBaseCatalog2s = pmsBaseCatalog2Mapper.selectByCatalog1Id(catalog1Id);
        return pmsBaseCatalog2s;
    }

    @Override
    public List<PmsBaseCatalog1> getAllPmsBaseCatalog1() {
        List<PmsBaseCatalog1> pmsBaseCatalog1s = pmsBaseCatalog1Mapper.selectAll();
        return pmsBaseCatalog1s;
    }
}
