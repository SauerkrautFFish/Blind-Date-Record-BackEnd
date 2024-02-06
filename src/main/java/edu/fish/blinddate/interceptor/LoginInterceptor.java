package edu.fish.blinddate.interceptor;

import edu.fish.blinddate.utils.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String jwt = request.getHeader("bd-token");
        Integer jwtUserId = JWTUtil.getUserIdByJwtToken(jwt);
        String userIdParams = request.getParameter("userId");
        if (jwtUserId == null || userIdParams == null || Integer.parseInt(userIdParams) != jwtUserId) {
            response.setStatus(401);
            return false;
        }

        return true;
    }
}
