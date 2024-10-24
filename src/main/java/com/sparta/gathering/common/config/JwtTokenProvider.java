package com.sparta.gathering.common.config;

import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import com.sparta.gathering.domain.user.enums.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtTokenProvider {

     private static final long TOKEN_TIME = 60 * 60 * 1000L; // 60분
    private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256; // 암호화 알고리즘
    private static final String BEARER_PREFIX = "Bearer "; // Bearer 접두어
    public static final String EMAIL_CLAIM = "email"; // 이메일 클레임
    public static final String USER_ROLE_CLAIM = "userRole"; // 유저 권한 클레임

    @Value("${jwt.secret}")
    private String secretKey;
    private Key key;

    @PostConstruct
    public void init() {
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalArgumentException("JWT secret key를 찾을 수 없습니다.");
        }

        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(decodedKey);
        log.info("JWT 서명 키가 정상적으로 설정되었습니다.");
    }

    public String createToken(UUID userId, String email, String nickname , UserRole userRole) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(userId.toString())
                        .claim(EMAIL_CLAIM, email)
                        .claim(USER_ROLE_CLAIM, userRole.name())
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME))
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                        .compact();
    }

    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(BEARER_PREFIX.length());
        }
        throw new BaseException(ExceptionEnum.JWT_TOKEN_NOT_FOUND);
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
