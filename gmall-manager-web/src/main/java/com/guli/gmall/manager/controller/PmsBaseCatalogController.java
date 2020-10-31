package com.guli.gmall.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.guli.gmall.bean.PmsBaseCatalog1;
import com.guli.gmall.bean.PmsBaseCatalog2;
import com.guli.gmall.bean.PmsBaseCatalog3;
import com.guli.gmall.service.PmsBaseCatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@CrossOrigin
public class PmsBaseCatalogController {

    @Reference(interfaceClass = PmsBaseCatalogService.class,version = "1.0.0",check = false)
    private PmsBaseCatalogService pmsBaseCatalogService;

    @RequestMapping(value = "/getCatalog3")
    public @ResponseBody List<PmsBaseCatalog3> getAllPmsBaseCatalog3(Integer catalog2Id){
        List<PmsBaseCatalog3> pmsBaseCatalog3s = pmsBaseCatalogService.getPmsBaseCatalog3(catalog2Id);
        return pmsBaseCatalog3s;
    }

    @RequestMapping(value = "/getCatalog2")
    public @ResponseBody List<PmsBaseCatalog2> getAllPmsBaseCatalog2(Integer catalog1Id){
        List<PmsBaseCatalog2> pmsBaseCatalog2s = pmsBaseCatalogService.getPmsBaseCatalog2(catalog1Id);
        return pmsBaseCatalog2s;
    }

    @RequestMapping(value = "/getCatalog1")
    public @ResponseBody List<PmsBaseCatalog1> getAllPmsBaseCatalog1(){
        List<PmsBaseCatalog1> pmsBaseCatalog1s = pmsBaseCatalogService.getAllPmsBaseCatalog1();
        return pmsBaseCatalog1s;
    }
}
