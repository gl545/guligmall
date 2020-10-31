package com.guli.gmall.interceptors;

import com.alibaba.fastjson.JSON;
import com.guli.gmall.annocation.LoginRequire;
import com.guli.gmall.util.CookieUtil;
import com.guli.gmall.util.HttpClientUtil;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        LoginRequire loginRequire = handlerMethod.getMethodAnnotation(LoginRequire.class);
        if(loginRequire==null){
            return true;
        }


        //需要拦截
        String token = "";
        String oldToken = CookieUtil.getCookieValue(request,"oldToken",true);
        String newToken  = request.getParameter("token");
        if(StringUtils.isNotBlank(oldToken)){
            token = oldToken;
        }
        if (StringUtils.isNotBlank(newToken)){
            token = newToken;
        }
        Map<String,String> map = new HashMap<>();
        String success = "fail";
        if(StringUtils.isNotBlank(token)){
            //需要去认证中心认证
            String successJson = HttpClientUtil.doGet("http://127.0.0.1:8085/verity?token="+token);
            map = JSON.parseObject(successJson, Map.class);
            success = map.get("status");



        }



        boolean loginSuccess = loginRequire.loginSuccess();
        if(loginSuccess){
            //必须登录成功才能使用
            if(!"success".equals(success)){
                //登录不成功，踢回认证中心
                response.sendRedirect("http://127.0.0.1:8085/index?ReturnUrl="+request.getRequestURL());
                return false;
            }
            //登录成功
            request.setAttribute("memberId",map.get("memberId"));
            request.setAttribute("nickname",map.get("nickname"));
            //将token写入cookie
            CookieUtil.setCookie(request,response,"oldToken",token,60*60*2,true);
        }else {
            if("success".equals(success)){
                request.setAttribute("memberId",map.get("memberId"));
                request.setAttribute("nickname",map.get("nickname"));
                //将token写入cookie
                CookieUtil.setCookie(request,response,"oldToken",token,60*60*2,true);
            }
        }

        return true;
    }
}
