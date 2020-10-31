package com.guli.gmall.manager.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.guli.gmall.bean.PmsSkuAttrValue;
import com.guli.gmall.bean.PmsSkuImage;
import com.guli.gmall.bean.PmsSkuInfo;
import com.guli.gmall.bean.PmsSkuSaleAttrValue;
import com.guli.gmall.manager.mapper.*;
import com.guli.gmall.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Service(interfaceClass = SkuService.class,version = "1.0.0",timeout = 15000)
public class SkuServiceImpl implements SkuService {

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Autowired
    private PmsSkuInfoMapper pmsSkuInfoMapper;

    @Autowired
    private PmsSkuAttrValueMapper pmsSkuAttrValueMapper;

    @Autowired
    private PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;

    @Autowired
    private PmsSkuImageMapper pmsSkuImageMapper;

    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(Long productId) {
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectSkuSaleAttrValueListBySpu(productId);
        return pmsSkuInfos;
    }

    public PmsSkuInfo getSkuInfoBySkuIdFromMysql(String skuId) {
        PmsSkuInfo pmsSkuInfo = pmsSkuInfoMapper.selectByPrimaryKey(Long.valueOf(skuId));
        List<PmsSkuImage> pmsSkuImages = pmsSkuImageMapper.selectBySkuId(skuId);
        pmsSkuInfo.setSkuImageList(pmsSkuImages);
        return pmsSkuInfo;
    }

    @Override
    public List<PmsSkuInfo> getSkuAll() {
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectSkuAll();
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {
            Long skuId = pmsSkuInfo.getId();
            List<PmsSkuAttrValue> pmsSkuAttrValues = pmsSkuAttrValueMapper.selectBySkuId(skuId);
            pmsSkuInfo.setSkuAttrValueList(pmsSkuAttrValues);
        }
        return pmsSkuInfos;
    }

    @Override
    public PmsSkuInfo getSkuInfoBySkuId(String skuId) {
        String skuKey = "sku:"+skuId+":info";
        String skuJson = (String) redisTemplate.opsForValue().get(skuKey);
        PmsSkuInfo pmsSkuInfo = null;
        if(skuJson!=null){
            pmsSkuInfo = JSON.parseObject(skuJson,PmsSkuInfo.class);
        }else {
            //设置分布式锁
            String token = UUID.randomUUID().toString();
            boolean flag = redisTemplate.opsForValue().setIfAbsent("sku:"+skuId+":lock",token,10,TimeUnit.SECONDS);
            if(flag){
                pmsSkuInfo = getSkuInfoBySkuIdFromMysql(skuId);
                if(pmsSkuInfo!=null){
                    redisTemplate.opsForValue().set(skuKey, JSON.toJSONString(pmsSkuInfo));
                }else{
                    //解决缓存穿透
                    redisTemplate.opsForValue().set(skuKey,"",3, TimeUnit.MINUTES);
                }
                //释放锁
                String lockToken = (String) redisTemplate.opsForValue().get("sku:"+skuId+":lock");
            if (lockToken!=null && lockToken.equals(token)){
                    redisTemplate.delete("sku:"+skuId+":lock");
                }
            }else{
                //设置失败,自旋
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return getSkuInfoBySkuId(skuId);
            }

        }
        return pmsSkuInfo;
    }

    @Override
    public String saveSkuInfo(PmsSkuInfo pmsSkuInfo) {
        //添加sku
        pmsSkuInfoMapper.insertSelective(pmsSkuInfo);
        Long skuId = pmsSkuInfo.getId();
        //添加sku属性关联
        List<PmsSkuAttrValue> pmsSkuAttrValues = pmsSkuInfo.getSkuAttrValueList();
        for (PmsSkuAttrValue pmsSkuAttrValue : pmsSkuAttrValues) {
            pmsSkuAttrValue.setSkuId(skuId);
            pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);
        }

        //添加销售属性
        List<PmsSkuSaleAttrValue> pmsSkuSaleAttrValues = pmsSkuInfo.getSkuSaleAttrValueList();
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : pmsSkuSaleAttrValues) {
            pmsSkuSaleAttrValue.setSkuId(skuId);
            pmsSkuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
        }
        //添加图片信息
        List<PmsSkuImage> pmsSkuImages = pmsSkuInfo.getSkuImageList();
        for (PmsSkuImage pmsSkuImage : pmsSkuImages) {
            pmsSkuImage.setSkuId(skuId);
            pmsSkuImageMapper.insertSelective(pmsSkuImage);
        }

        return "success";
    }


}
