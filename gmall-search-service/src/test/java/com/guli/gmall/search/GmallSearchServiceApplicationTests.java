package com.guli.gmall.search;

import com.guli.gmall.bean.PmsSkuInfo;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
//@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallSearchServiceApplicationTests {

    @Autowired
    private JestClient jestClient;

    @Test
    public void put() {
        Index index = new Index.Builder(null).index("gmall").type("SkuInfo").build();
        try {
            jestClient.execute(index);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void search(){
        Search search = new Search.Builder("{\n" +
                "  \"query\":{\n" +
                "    \"match\": {\n" +
                "      \"name\": \"operation red sea\"\n" +
                "    }\n" +
                "  }\n" +
                "}").addIndex("movie_index").addType("movie").build();
        try {
            SearchResult result = jestClient.execute(search);
            System.out.println(result.getJsonString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
