package com.guli.gmall.user.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.guli.gmall.bean.UmsMember;
import com.guli.gmall.bean.UmsMemberReceiveAddress;
import com.guli.gmall.service.UmsMemberService;
import com.guli.gmall.user.mapper.UmsMemberMapper;
import com.guli.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Service(interfaceClass = UmsMemberService.class, version = "1.0.0", timeout = 15000)
public class UmsMemberServiceImpl implements UmsMemberService {

    @Autowired
    private UmsMemberMapper umsMemberMapper;

    @Autowired
    private UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;

    @Autowired
    RedisTemplate<Object,Object> redisTemplate;

    @Override
    public List<UmsMember> getAllUser() {

        List<UmsMember> umsMembers = umsMemberMapper.selectAll();

        return umsMembers;
    }

    @Override
    public UmsMember CheckUser(String uid) {
        UmsMember umsMember = umsMemberMapper.selectUserByUid(uid);
        return umsMember;
    }

    @Override
    public UmsMemberReceiveAddress getReceiveAddressById(String userAddressId) {
        UmsMemberReceiveAddress umsMemberReceiveAddress = umsMemberReceiveAddressMapper.selectByPrimaryKey(Long.valueOf(userAddressId));
        return umsMemberReceiveAddress;
    }

    @Override
    public List<UmsMemberReceiveAddress> getUserAddrList(String memberId) {
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberReceiveAddressMapper.selectByMemberId(memberId);
        return umsMemberReceiveAddresses;
    }

    @Override
    public UmsMember addOauthUser(UmsMember umsMember) {
        umsMemberMapper.insertSelective(umsMember);
        return umsMember;

    }

    @Override
    public void addToken(String token,String memberId) {

        redisTemplate.opsForValue().set("user:"+memberId+"token",token,60*60*2, TimeUnit.SECONDS);
    }

    @Override
    public UmsMember login(UmsMember umsMember) {
        String password = umsMember.getPassword();
        String username = umsMember.getUsername();
        String umsMemberStr = (String) redisTemplate.opsForValue().get("user:"+password+username+":info");
        if(StringUtils.isNotBlank(umsMemberStr)){
            UmsMember user = JSON.parseObject(umsMemberStr, UmsMember.class);
            return user;
        }else {
            UmsMember user = loginFormDb(umsMember);
            redisTemplate.opsForValue().set("user:"+password+username+":info",JSON.toJSONString(user),60*60*24,TimeUnit.SECONDS);
            return user;
        }

    }

    public UmsMember loginFormDb(UmsMember umsMember) {
        List<UmsMember> umsMembers = umsMemberMapper.selectUser(umsMember);
        UmsMember user = null;
        if (umsMembers.size()!=0) {
            user = umsMembers.get(0);
        }

        return user;
    }
}
