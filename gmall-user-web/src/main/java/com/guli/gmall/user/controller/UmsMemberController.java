package com.guli.gmall.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.guli.gmall.bean.UmsMember;
import com.guli.gmall.service.UmsMemberService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
@Controller
public class UmsMemberController {

    @Reference(interfaceClass = UmsMemberService.class,version = "1.0.0",check = false)
    private UmsMemberService umsMemberService;

    @RequestMapping(value = "/getAllUser")
    @ResponseBody
    public List<UmsMember> getAllUser(){
        List<UmsMember> umsMembers = umsMemberService.getAllUser();
        return umsMembers;
    }
}
