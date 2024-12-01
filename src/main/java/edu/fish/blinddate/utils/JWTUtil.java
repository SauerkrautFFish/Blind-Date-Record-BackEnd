package edu.fish.blinddate.utils;

import edu.fish.blinddate.interceptor.LoginInterceptor;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

/**
 * @author akaTom
 * @since 2022/8/5
 */
public class JWTUtil {

    private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);
    public static final long EXPIRE = 1000 * 60 * 60 * 2;
    public static final String APP_SECRET = "ukBfda1fDa2dDaZd24asFfWuWs2jZWLZHO";
    public static final Key signKey = new SecretKeySpec(APP_SECRET.getBytes(), SignatureAlgorithm.HS256.getJcaName());

    private JWTUtil() {
        throw new UnsupportedOperationException();
    }

    public static String getJwtToken(Integer id) {

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")

                .setSubject("bd-user")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))

                .claim("id", id)  //设置token主体部分 ，存储用户信息
                .signWith(signKey)
                .compact();
    }

    public static Integer getUserIdByJwtToken(String jwtToken) {
        if(jwtToken == null || jwtToken.equals("")) return null;

        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(signKey).parseClaimsJws(jwtToken);

            Claims claims = claimsJws.getBody();
            return (Integer) claims.get("id");
        } catch (ExpiredJwtException | SignatureException e) {
            // 过期 or 不符合预期的token
        } catch (Exception e) {
            logger.error("jwt error, input param = {}, error msg = {}", jwtToken, e.getMessage());
        }

        return null;
    }
}
