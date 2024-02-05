package edu.fish.blinddate.interceptor;

import edu.fish.blinddate.utils.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //用户登录判断业务
        String jwt = request.getHeader("bd-token");

        Integer userId = JWTUtil.getUserIdByJwtToken(jwt);
        if (userId == null) {
            response.setStatus(401);
            return false;
        }

        return true;
    }
}
