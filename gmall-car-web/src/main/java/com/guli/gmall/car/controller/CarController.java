package com.guli.gmall.car.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.guli.gmall.annocation.LoginRequire;
import com.guli.gmall.bean.OmsCartItem;
import com.guli.gmall.bean.PmsSkuInfo;
import com.guli.gmall.service.CarService;
import com.guli.gmall.service.SkuService;
import com.guli.gmall.util.CookieUtil;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class CarController {

    @Reference(interfaceClass = SkuService.class,version = "1.0.0",check = false)
    private SkuService skuService;

    @Reference(interfaceClass = CarService.class,version = "1.0.0",check = false)
    private CarService carService;


    @LoginRequire(loginSuccess = false)
    @RequestMapping("/checkCart")
    public String checkCart(String isChecked,Long skuId,ModelMap modelMap,HttpServletRequest request){
        String memberId = (String) request.getAttribute("memberId");
        OmsCartItem omsCartItem = new OmsCartItem();
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        if(StringUtils.isNotBlank(memberId)){
            omsCartItem.setMemberId(Long.valueOf(memberId));
            omsCartItem.setProductSkuId(skuId);
            omsCartItem.setIsChecked(isChecked);
            carService.checkCart(omsCartItem);
            omsCartItems = carService.getCarList(memberId);
        }else {
            String carListCookie = CookieUtil.getCookieValue(request, "carListCookie", true);
            if(StringUtils.isNotBlank(carListCookie)){
                omsCartItems = JSON.parseArray(carListCookie, OmsCartItem.class);

            }
        }



        modelMap.put("cartList",omsCartItems);
        BigDecimal totalAmount = getTotalAmount(omsCartItems);
        modelMap.put("totalAmount",totalAmount);
        return "cartListInner";
    }

    @LoginRequire(loginSuccess = false)
    @RequestMapping("cartList")
    public String cartList(ModelMap modelMap,HttpServletRequest request){
        String memberId = (String) request.getAttribute("memberId");
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        if(StringUtils.isNotBlank(memberId)){
            omsCartItems = carService.getCarList(memberId);
        }else {
            String carListCookie = CookieUtil.getCookieValue(request, "carListCookie", true);
            if(StringUtils.isNotBlank(carListCookie)){
                omsCartItems = JSON.parseArray(carListCookie, OmsCartItem.class);
            }
        }

        for (OmsCartItem omsCartItem : omsCartItems) {
            omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(BigDecimal.valueOf(omsCartItem.getQuantity())));
        }

        modelMap.put("cartList",omsCartItems);

        BigDecimal totalAmount = getTotalAmount(omsCartItems);
        modelMap.put("totalAmount",totalAmount);
        return "cartList";
    }

    private BigDecimal getTotalAmount(List<OmsCartItem> omsCartItems) {
        BigDecimal totalAmount = new BigDecimal("0");
        for (OmsCartItem omsCartItem : omsCartItems) {

            if("1".equals(omsCartItem.getIsChecked())){
                totalAmount = totalAmount.add(omsCartItem.getTotalPrice());
            }
        }
        return totalAmount;
    }

    @LoginRequire(loginSuccess = false)
    @RequestMapping("/addToCart")
    public String addToCart(Integer num, String skuId, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response){
        String memberId = (String) request.getAttribute("memberId");
        PmsSkuInfo pmsSkuInfo = skuService.getSkuInfoBySkuId(skuId);
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setCreateDate(new Date());
        omsCartItem.setPrice(pmsSkuInfo.getPrice());
        omsCartItem.setProductId(pmsSkuInfo.getProductId());
        omsCartItem.setProductPic(pmsSkuInfo.getSkuDefaultImg());
        omsCartItem.setProductName(pmsSkuInfo.getSkuName());
        omsCartItem.setProductCategoryId(pmsSkuInfo.getProductId());
        omsCartItem.setQuantity(num);
        omsCartItem.setProductSkuId(Long.valueOf(skuId));

        List<OmsCartItem> omsCartItems = new ArrayList<>();

        if(StringUtils.isBlank(memberId)){
            //用户未登陆走cookie
            //cookie中原有的购物车数据
            String carListCookie = CookieUtil.getCookieValue(request, "carListCookie", true);
            if(StringUtils.isBlank(carListCookie)){
                omsCartItems.add(omsCartItem);
            }else{

                omsCartItems = JSON.parseArray(carListCookie, OmsCartItem.class);
                boolean exist = if_car_exist(omsCartItems,omsCartItem);
                if(exist){
                    //cookie中有该商品，更新购物车
                    for (OmsCartItem cartItem : omsCartItems) {
                        Long productSkuId = cartItem.getProductSkuId();
                        if(productSkuId==omsCartItem.getProductSkuId()){
                            cartItem.setQuantity(cartItem.getQuantity()+omsCartItem.getQuantity());
                            cartItem.setPrice(omsCartItem.getPrice());
                            //cartItem.setTotalPrice(omsCartItem.getPrice().add(BigDecimal.valueOf(omsCartItem.getQuantity())));
                        }
                    }
                }else {
                    omsCartItems.add(omsCartItem);
                }
            }

            CookieUtil.setCookie(request,response,"carListCookie", JSON.toJSONString(omsCartItems),60*60*72,true);

        }else{
            omsCartItem.setMemberId(Long.valueOf(memberId));
            //用户已登陆
            OmsCartItem omsCartItemFromDb = carService.getCarExistByUser(memberId,skuId);
            if(omsCartItemFromDb==null){
                //添加购物车
                carService.addCar(omsCartItem);
            }else {
                //更新购物车
                omsCartItemFromDb.setQuantity(omsCartItemFromDb.getQuantity()+omsCartItem.getQuantity());
                carService.updateCar(omsCartItemFromDb);
            }
            //同步缓存
            carService.flushCarCache(memberId);
        }
        return "redirect:/success.html";
    }

    private boolean if_car_exist(List<OmsCartItem> omsCartItems, OmsCartItem omsCartItem) {
        for (OmsCartItem cartItem : omsCartItems) {
            Long productSkuId = cartItem.getProductSkuId();
            if(productSkuId==omsCartItem.getProductSkuId()){
                return true;
            }

        }
        return false;
    }
}
