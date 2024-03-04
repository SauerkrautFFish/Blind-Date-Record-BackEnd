package edu.fish.blinddate.interceptor;

import edu.fish.blinddate.utils.JWTUtil;
import edu.fish.blinddate.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 预检请求
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }

        String jwt = request.getHeader("bd-token");
        Integer jwtUserId = JWTUtil.getUserIdByJwtToken(jwt);
        if (jwtUserId == null) {
            response.setStatus(401);
            return false;
        }
        UserContext.setUserId(jwtUserId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        UserContext.removeUserId();
    }
}
