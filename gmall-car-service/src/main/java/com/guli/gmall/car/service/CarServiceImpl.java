package com.guli.gmall.car.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.guli.gmall.bean.OmsCartItem;
import com.guli.gmall.car.mapper.OmsCartItemMapper;
import com.guli.gmall.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Service(interfaceClass = CarService.class,version = "1.0.0",timeout = 15000)
public class CarServiceImpl implements CarService {


    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Autowired
    private OmsCartItemMapper omsCartItemMapper;


    @Override
    public List<OmsCartItem> getCarList(String memberId) {
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        List<Object> values = redisTemplate.opsForHash().values("user:" + memberId + ":car");
        for (Object value : values) {
            OmsCartItem omsCartItem = JSON.parseObject(String.valueOf(value), OmsCartItem.class);
            omsCartItems.add(omsCartItem);
        }

        return omsCartItems;
    }

    @Override
    public OmsCartItem getCarExistByUser(String memberId, String skuId) {
        OmsCartItem omsCartItem = omsCartItemMapper.selectCarExistByUser(memberId,skuId);
        return omsCartItem;
    }

    @Override
    public void addCar(OmsCartItem omsCartItem) {
        omsCartItemMapper.insertSelective(omsCartItem);
    }

    @Override
    public void updateCar(OmsCartItem omsCartItemFromDb) {
        omsCartItemMapper.updateByPrimaryKey(omsCartItemFromDb);
    }

    @Override
    public void checkCart(OmsCartItem omsCartItem) {
        omsCartItemMapper.updateBySkuId(omsCartItem);
        flushCarCache(String.valueOf(omsCartItem.getMemberId()));
    }

    @Override
    public void flushCarCache(String memberId) {
        //redisTemplate.opsForHash().delete("user:"+memberId+":car");
        List<OmsCartItem> omsCartItems = omsCartItemMapper.selectByMemberId(memberId);
        Map<String,String> map = new HashMap<>();
        for (OmsCartItem omsCartItem : omsCartItems) {
            if(omsCartItem!=null){
                omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(BigDecimal.valueOf(omsCartItem.getQuantity())));
                String skuId = String.valueOf(omsCartItem.getProductSkuId());
                map.put(skuId, JSON.toJSONString(omsCartItem));
            }
        }
        redisTemplate.delete("user:"+memberId+":car");
        redisTemplate.opsForHash().putAll("user:"+memberId+":car",map);

    }
}
