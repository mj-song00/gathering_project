package com.sparta.gathering.common.config.jwt;

import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.user.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class JwtTokenProvider {

    public static final String EMAIL_CLAIM = "email"; // JWT 이메일 클레임 키
    public static final String USER_ROLE_CLAIM = "userRole"; // JWT 유저 권한 클레임 키
    private static final String BEARER_PREFIX = "Bearer "; // Authorization 헤더의 Bearer 접두어
    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256; // 서명 알고리즘
    private static final int MIN_SECRET_KEY_LENGTH = 32; // 시크릿 키 최소 길이 (256비트)

    @Value("${jwt.expiration}")
    private long expirationTime;

    @Value("${jwt.secret}")
    private String secretKey;

    private Key key; // 암호화에 사용할 Key 객체

    // 시크릿 키 길이 및 초기화 검증
    @PostConstruct
    public void init() {
        validateSecretKeyLength();
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(decodedKey);
    }

    // 시크릿 키 길이 및 유효성 검증
    private void validateSecretKeyLength() {
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalArgumentException("JWT secret key를 찾을 수 없습니다.");
        }
        if (secretKey.length() < MIN_SECRET_KEY_LENGTH) {
            throw new IllegalArgumentException("JWT 시크릿 키는 최소 256비트(32자) 이상이어야 합니다.");
        }
    }

    // JWT 토큰 생성
    public String createToken(UUID userId, String email, UserRole userRole) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime * 1000); // 밀리초로 변환
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim(EMAIL_CLAIM, email)
                .claim(USER_ROLE_CLAIM, userRole.name())
                .setIssuedAt(now) // 발급 시간
                .setExpiration(expiryDate) // 만료 시간
                .signWith(key, SIGNATURE_ALGORITHM) // 서명 설정
                .compact();
    }

    // Bearer 접두어 제거 후 토큰 반환
    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(BEARER_PREFIX.length());
        }
        throw new BaseException(ExceptionEnum.JWT_TOKEN_NOT_FOUND);
    }

    // JWT 토큰에서 Claims 추출
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
