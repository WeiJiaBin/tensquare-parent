package com.tenquare.qa.interceptor;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import util.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        System.out.println("经过了拦截器");
        //无论无何都放行，具体能不能操作要看具体的操作区判断
        //拦截器只是负责把头请求头中包含tokend的令牌进行一个解析验证
        String header = request.getHeader("Authorization");

        if (header != null && !"".equals(header)) {
            //得到token
            if (header.startsWith("Bearer ")) {
                String token = header.substring(7);
               // System.out.println("令牌为："+token);
                //对令牌进行验证
                try {
                    Claims claims = jwtUtil.parseJWT(token);
                    String roles = (String) claims.get("roles");
                    System.out.println("角色为："+roles);
                    if (roles != null && roles.equals("admin")) {
                        request.setAttribute("claims_admin",token);
                    }
                    if (roles != null && roles.equals("user")) {
                       // System.out.println("存了user角色");
                        request.setAttribute("claims_user",token);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("令牌不正确！");
                }
            }
        }
        return true;
    }
}
