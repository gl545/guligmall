package com.guli.gmall.search.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.guli.gmall.bean.PmsSearchSkuInfo;
import com.guli.gmall.bean.PmsSkuInfo;
import com.guli.gmall.bean.SkuSearchParam;
import com.guli.gmall.service.SearchService;
import com.guli.gmall.service.SkuService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Service(interfaceClass = SearchService.class, version = "1.0.0", timeout = 15000)
public class SearchServiceImpl implements SearchService {

    @Reference(interfaceClass = SkuService.class, version = "1.0.0", check = false)
    private SkuService skuService;

    @Autowired
    private JestClient jestClient;

    @Override
    public List<PmsSearchSkuInfo> getSkuId(SkuSearchParam skuSearchParam) {

        Long catalog3Id = skuSearchParam.getCatalog3Id();
        String keyword = skuSearchParam.getKeyword();
        String[] valueIds = skuSearchParam.getValueId();

        //jest的dsl工具
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //bool
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        //filter
        if(catalog3Id != null){
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id",catalog3Id);
            boolQueryBuilder.filter(termQueryBuilder);
        }
        if(valueIds != null){
            for (String valueId : valueIds) {
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId",valueId);
                boolQueryBuilder.filter(termQueryBuilder);
            }

        }

        //must
        if(keyword != null && !keyword.equals("")){
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName",keyword);
            boolQueryBuilder.must(matchQueryBuilder);
        }

        //query
        searchSourceBuilder.query(boolQueryBuilder);
        //from
        searchSourceBuilder.from(0);
        //size
        searchSourceBuilder.size(200);
        //sort
        searchSourceBuilder.sort("id", SortOrder.DESC);
        //highlight
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.field("skuName");
        highlightBuilder.postTags("</span>");
       searchSourceBuilder.highlighter(highlightBuilder);

        Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex("gmall").addType("PmsSkuInfo").build();
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();
        try {
            SearchResult searchResult = jestClient.execute(search);
            List<SearchResult.Hit<PmsSearchSkuInfo, Void>> searchResultHits = searchResult.getHits(PmsSearchSkuInfo.class);
            for (SearchResult.Hit<PmsSearchSkuInfo, Void> searchResultHit : searchResultHits) {
                PmsSearchSkuInfo pmsSearchSkuInfo = searchResultHit.source;
                Map<String, List<String>> highlight = searchResultHit.highlight;
                if(highlight != null){
                    String skuName = highlight.get("skuName").get(0);
                    pmsSearchSkuInfo.setSkuName(skuName);
                }
                pmsSearchSkuInfos.add(pmsSearchSkuInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pmsSearchSkuInfos;
    }



    public void put() {
        List<PmsSkuInfo> pmsSkuInfos = skuService.getSkuAll();
        //List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();
        PmsSearchSkuInfo pmsSearchSkuInfo = new PmsSearchSkuInfo();
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {

            BeanUtils.copyProperties(pmsSkuInfo, pmsSearchSkuInfo);
            //pmsSearchSkuInfos.add(pmsSearchSkuInfo);
            Index index = new Index.Builder(pmsSearchSkuInfo).index("gmall").type("PmsSkuInfo").id(String.valueOf(pmsSearchSkuInfo.getId())).build();
            try {
                jestClient.execute(index);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
