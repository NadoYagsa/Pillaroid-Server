package com.nadoyagsa.pillaroid.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class AuthTokenProvider implements InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(AuthTokenProvider.class);

    private final String secretKey;
    private Key key;

    public AuthTokenProvider(@Value("${jwt.secret-key}") String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 토큰의 payload에 회원 번호를 삽입
    public String createAuthToken(Long userIdx) {
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer("pillaroid")
                .setIssuedAt(new Date())
                .claim("userId", userIdx)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰으로부터 payload를 추출하는 메서드
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);

            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            logger.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            logger.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            logger.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            logger.info("JWT 토큰이 잘못되었습니다.");
        } catch (Exception e) {
            logger.info("서비스에 접근할 수 없는 토큰입니다.");
        }
        return false;
    }
}
