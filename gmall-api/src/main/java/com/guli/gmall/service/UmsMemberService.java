package com.guli.gmall.service;

import com.guli.gmall.bean.UmsMember;
import com.guli.gmall.bean.UmsMemberReceiveAddress;

import java.util.List;

public interface UmsMemberService {
    List<UmsMember> getAllUser();

    UmsMember login(UmsMember umsMember);

    void addToken(String token, String memberId);

    UmsMember CheckUser(String uid);

    UmsMember addOauthUser(UmsMember umsMember);

    List<UmsMemberReceiveAddress> getUserAddrList(String memberId);

    UmsMemberReceiveAddress getReceiveAddressById(String userAddressId);
}
