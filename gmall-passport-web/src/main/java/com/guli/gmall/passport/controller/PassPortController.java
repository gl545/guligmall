package com.guli.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.guli.gmall.bean.UmsMember;
import com.guli.gmall.service.UmsMemberService;
import com.guli.gmall.util.HttpClientUtil;
import com.guli.gmall.util.JwtUtil;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassPortController {


    @Reference(interfaceClass = UmsMemberService.class,version = "1.0.0",check = false)
    private UmsMemberService umsMemberService;

    @RequestMapping("/vlogin")
    public String vlogin(String code,HttpServletRequest request){
        Map<String,String> map = new HashMap<>();
        map.put("client_id","2220113332");
        map.put("client_secret","aa681b00c35b61eb6e100185681da527");
        map.put("grant_type","authorization_code");
        map.put("redirect_uri","http://127.0.0.1:8085/vlogin");
        map.put("code",code);
        String doPost = HttpClientUtil.doPost("https://api.weibo.com/oauth2/access_token?", map);
        Map successMap = JSON.parseObject(doPost, Map.class);
        String access_token = (String) successMap.get("access_token");
        String uid = (String) successMap.get("uid");

        //获取用户信息
        String userJsonStr = HttpClientUtil.doGet("https://api.weibo.com/2/users/show.json?access_token="+access_token+"&uid="+uid);
        Map user = JSON.parseObject(userJsonStr, Map.class);

        //查询数据库有没有该用户
        UmsMember umsMemberCheck = umsMemberService.CheckUser(uid);


        UmsMember umsMember = new UmsMember();
        if(umsMemberCheck==null){
            //将数据存入数据库
            umsMember.setCreateTime(new Date());
            umsMember.setAccessCode(code);
            umsMember.setAccessToken(access_token);
            umsMember.setSourceType(2);
            umsMember.setSourceUid(Long.valueOf(uid));
            umsMember.setNickname((String) user.get("screen_name"));
            umsMember.setCity((String) user.get("location"));
            umsMember = umsMemberService.addOauthUser(umsMember);
        }else {
            umsMember = umsMemberCheck;
        }
        //生成token
        String token = "";
        String memberId = String.valueOf(umsMember.getId());
        String nickname = umsMember.getNickname();
        Map<String,Object> userMap = new HashMap<>();
        userMap.put("memberId",memberId);
        userMap.put("nickname",nickname);

        String ip = request.getHeader("x-forwarded-for");
        if(StringUtils.isBlank(ip)){
            ip = request.getRemoteAddr();
            if(StringUtils.isBlank(ip)){
                ip = "127.0.0.1";
            }
        }
        token = JwtUtil.encode("gmall",userMap,ip);
        //存入redis
        umsMemberService.addToken(token,memberId);

        return "redirect:http://127.0.0.1:8083/index?token="+token;
    }

    @RequestMapping("verity")
    @ResponseBody
    public String verity(String token,HttpServletRequest request){
        String ip = request.getHeader("x-forwarded-for");
        if(StringUtils.isBlank(ip)){
            ip = request.getRemoteAddr();
            if(StringUtils.isBlank(ip)){
                ip = "127.0.0.1";
            }
        }
        Map<String,String> map = new HashMap<>();
        Map<String, Object> gmall = JwtUtil.decode(token, "gmall", ip);
        if(gmall!=null){
            map.put("status","success");
            map.put("memberId", (String) gmall.get("memberId"));
            map.put("nickname", (String) gmall.get("nickname"));
        }


        return JSON.toJSONString(map);
    }

    @RequestMapping("login")
    @ResponseBody
    public String login(UmsMember umsMember, HttpServletRequest request){

        String token = "";
        UmsMember user = umsMemberService.login(umsMember);
        Map<String,Object> map = new HashMap<>();
        if(user!=null){
            String memberId = String.valueOf(user.getId());
            String nickname = user.getNickname();
            map.put("memberId",memberId);
            map.put("nickname",nickname);

            String ip = request.getHeader("x-forwarded-for");
            if(StringUtils.isBlank(ip)){
                ip = request.getRemoteAddr();
                if(StringUtils.isBlank(ip)){
                    ip = "127.0.0.1";
                }
            }
            token = JwtUtil.encode("gmall",map,ip);

            //将token存入redis
            umsMemberService.addToken(token,memberId);
        }else {
            token = "fail";
        }


        return token;
    }

    @RequestMapping("index")
    public String index(String ReturnUrl, ModelMap modelMap){
        modelMap.put("ReturnUrl",ReturnUrl);
        return "index";
    }
}
