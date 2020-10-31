package com.guli.gmall.service;

import com.guli.gmall.bean.OmsCartItem;

import java.util.List;

public interface CarService {
    OmsCartItem getCarExistByUser(String memberId, String skuId);

    void addCar(OmsCartItem omsCartItem);

    void updateCar(OmsCartItem omsCartItemFromDb);

    void flushCarCache(String memberId);

    List<OmsCartItem> getCarList(String memberId);

    void checkCart(OmsCartItem omsCartItem);
}
