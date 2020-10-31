package com.guli.gmall.search.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.guli.gmall.annocation.LoginRequire;
import com.guli.gmall.bean.*;
import com.guli.gmall.service.AttrService;
import com.guli.gmall.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class SearchController {

    @Reference(interfaceClass = SearchService.class, version = "1.0.0", check = false)
    private SearchService searchService;

    @Reference(interfaceClass = AttrService.class, version = "1.0.0", check = false)
    private AttrService attrService;

    @RequestMapping("list.html")
    public String list(SkuSearchParam skuSearchParam, Model model) {
        String keyword = skuSearchParam.getKeyword();
        if ("".equals(keyword)) {
            return "list";
        }
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = searchService.getSkuId(skuSearchParam);
        model.addAttribute("skuLsInfoList", pmsSearchSkuInfos);

        //显示商品属性
        Set<String> set = new HashSet<>();
        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
            List<PmsSkuAttrValue> skuAttrValueList = pmsSearchSkuInfo.getSkuAttrValueList();
            for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
                if (pmsSkuAttrValue != null) {
                    String valueId = String.valueOf(pmsSkuAttrValue.getValueId());
                    set.add(valueId);
                }
            }
        }
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = attrService.getAttrList(set);
        model.addAttribute("attrList", pmsBaseAttrInfos);


        String[] delValueIds = skuSearchParam.getValueId();
        List<PmsSearchCrumb> pmsSearchCrumbs = new ArrayList<>();
        if (delValueIds != null) {
            for (String delValueId : delValueIds) {
                PmsSearchCrumb pmsSearchCrumb = new PmsSearchCrumb();
                pmsSearchCrumb.setValueId(delValueId);
                pmsSearchCrumb.setUrlParam(getUrlParamForCrumb(skuSearchParam,delValueId));
                Iterator<PmsBaseAttrInfo> iterator = pmsBaseAttrInfos.iterator();
                while (iterator.hasNext()) {
                    PmsBaseAttrInfo pmsBaseAttrInfo = iterator.next();
                    List<PmsBaseAttrValue> pmsBaseAttrValues = pmsBaseAttrInfo.getAttrValueList();
                    for (PmsBaseAttrValue pmsBaseAttrValue : pmsBaseAttrValues) {
                        String valueId = String.valueOf(pmsBaseAttrValue.getId());
                        if (delValueId.equals(valueId)) {
                            pmsSearchCrumb.setValueName(pmsBaseAttrValue.getValueName());
                            iterator.remove();
                        }
                    }
                }
                pmsSearchCrumbs.add(pmsSearchCrumb);

            }
        }

        //面包屑
        model.addAttribute("attrValueSelectedList", pmsSearchCrumbs);

        //url参数
        String urlParam = getUrlParam(skuSearchParam);
        model.addAttribute("urlParam", urlParam);
        return "list";
    }

    private String getUrlParamForCrumb(SkuSearchParam skuSearchParam, String delValueId) {
        Long catalog3Id = skuSearchParam.getCatalog3Id();
        String keyword = skuSearchParam.getKeyword();
        String[] valueIds = skuSearchParam.getValueId();

        String urlParam = "";
        if (catalog3Id != null) {
            if (urlParam != null && !urlParam.equals("")) {
                urlParam += "&";
            }
            urlParam += "catalog3Id=" + catalog3Id;
        }

        if (keyword != null) {
            if (urlParam != null && !urlParam.equals("")) {
                urlParam += "&";
            }
            urlParam += "keyword=" + keyword;
        }

        if (valueIds != null) {
            for (String valueId : valueIds) {
                if(!valueId.equals(delValueId)){
                    urlParam += "&valueId=" + valueId;
                }

            }
        }

        return urlParam;
    }

    private String getUrlParam(SkuSearchParam skuSearchParam) {
        Long catalog3Id = skuSearchParam.getCatalog3Id();
        String keyword = skuSearchParam.getKeyword();
        String[] valueIds = skuSearchParam.getValueId();

        String urlParam = "";
        if (catalog3Id != null) {
            if (urlParam != null && !urlParam.equals("")) {
                urlParam += "&";
            }
            urlParam += "catalog3Id=" + catalog3Id;
        }

        if (keyword != null) {
            if (urlParam != null && !urlParam.equals("")) {
                urlParam += "&";
            }
            urlParam += "keyword=" + keyword;
        }

        if (valueIds != null) {
            for (String valueId : valueIds) {
                urlParam += "&valueId=" + valueId;
            }
        }

        return urlParam;
    }

    @LoginRequire(loginSuccess = false)
    @RequestMapping("index")
    public String index() {
        return "index";
    }

    @RequestMapping("put")
    @ResponseBody
    public String test() {
        searchService.put();
        return "put success";
    }
}
