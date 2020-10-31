package com.guli.gmall.order.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.guli.gmall.bean.OmsOrder;
import com.guli.gmall.bean.OmsOrderItem;
import com.guli.gmall.bean.PmsSkuInfo;
import com.guli.gmall.order.mapper.OmsOrderItemMapper;
import com.guli.gmall.order.mapper.OmsOrderMapper;
import com.guli.gmall.service.CarService;
import com.guli.gmall.service.OrderService;
import com.guli.gmall.service.SkuService;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Service(interfaceClass = OrderService.class,version = "1.0.0",timeout = 15000)
public class OrderServiceImpl implements OrderService {

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Autowired
    private OmsOrderMapper omsOrderMapper;

    @Autowired
    private OmsOrderItemMapper omsOrderItemMapper;

    @Reference(interfaceClass = SkuService.class,version = "1.0.0",check = false)
    private SkuService skuService;

    @Reference(interfaceClass = CarService.class,version = "1.0.0",check = false)
    private CarService carService;

    @Override
    public void updateProcessStatus(OmsOrder omsOrder) {
        omsOrderMapper.updateProcessStatus(omsOrder);
    }

    @Override
    public OmsOrder getOrderInfoByOutTradeNo(String outTradeNo) {
        OmsOrder omsOrder = omsOrderMapper.selectByOutTradeNo(outTradeNo);
        return omsOrder;
    }

    @Override
    public void saveOrder(OmsOrder omsOrder) {
        omsOrderMapper.insertSelective(omsOrder);
        Long id = omsOrder.getId();
        List<OmsOrderItem> omsOrderItems = omsOrder.getOmsOrderItems();
        for (OmsOrderItem omsOrderItem : omsOrderItems) {
            omsOrderItem.setOrderId(id);
            omsOrderItemMapper.insertSelective(omsOrderItem);
        }
        //carService.delect();

    }

    @Override
    public boolean checkPrice(Long productSkuId, BigDecimal price) {
        PmsSkuInfo skuInfo = skuService.getSkuInfoBySkuId(String.valueOf(productSkuId));
        BigDecimal skuPrice = skuInfo.getPrice();
        if(price.compareTo(skuPrice)==0){
            return true;
        }

        return false;
    }

    @Override
    public boolean checkTradeCode(String memberId, String tradeCode) {
        String tradeKey = "user:"+memberId+":tradeCode";
        String tradeCodeFormCache = (String) redisTemplate.opsForValue().get(tradeKey);
        if(StringUtils.isNotBlank(tradeCodeFormCache)&&tradeCodeFormCache.equals(tradeCode)){
            redisTemplate.delete(tradeKey);
            return true;
        }
        return false;
    }

    @Override
    public String getTradeCode(String memberId) {
        String tradeCode = UUID.randomUUID().toString();
        String tradeKey = "user:"+memberId+":tradeCode";
        redisTemplate.opsForValue().set(tradeKey,tradeCode,60*15, TimeUnit.SECONDS);
        return tradeCode;
    }

}
