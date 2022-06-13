package org.joe.cloud.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Tianze Zhu
 * @since 2022-05-02
 */
@Slf4j
@Component
public class JwtProcessor {

    private static final String SECRET = "fdsafOvferCdsaloud-fdSeffcrewet";
    private static final Long EXPIRATION = 86400000L;

    public static String generateToken(String username) {
        return Jwts.builder()
                .setAudience(username)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    public static Boolean verify(String token) {
        return getClaimsByToken(token) != null;
    }

    public static String getUsernameByToken(String token) {
        return getClaimsByToken(token).getAudience();
    }

    private static Claims getClaimsByToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }
}
