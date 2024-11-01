package com.sparta.gathering.common.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.domain.user.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain)
            throws IOException, ServletException {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            try {
                Claims claims = jwtTokenProvider.extractClaims(jwtTokenProvider.substringToken(bearerToken));
                setAuthentication(claims);
            } catch (SecurityException | NullPointerException e) {
                sendErrorResponse(response, ExceptionEnum.JWT_TOKEN_NOT_FOUND, e);
                return;
            } catch (SignatureException | MalformedJwtException e) {
                sendErrorResponse(response, ExceptionEnum.INVALID_JWT_SIGNATURE, e);
                return;
            } catch (ExpiredJwtException e) {
                sendErrorResponse(response, ExceptionEnum.EXPIRED_JWT_TOKEN, e);
                return;
            } catch (UnsupportedJwtException e) {
                sendErrorResponse(response, ExceptionEnum.UNSUPPORTED_JWT_TOKEN, e);
                return;
            } catch (IllegalArgumentException e) {
                sendErrorResponse(response, ExceptionEnum.INVALID_JWT_TOKEN, e);
                return;
            } catch (Exception e) {
                sendErrorResponse(response, ExceptionEnum.INTERNAL_SERVER_ERROR, e);
                return;
            }
        }
        chain.doFilter(request, response);
    }

    // 클라이언트에게 오류 응답 전송
    private void sendErrorResponse(HttpServletResponse response, ExceptionEnum exception,
            Exception e) throws IOException {
        log.error("JWT 인증 오류 - {}", exception.getMessage(), e);
        // SecurityContextHolder 인증 정보 클리어
        SecurityContextHolder.clearContext();
        log.info("SecurityContextHolder 인증 정보 클리어 완료");
        // 클라이언트 응답 구성
        response.setStatus(exception.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        // 클라이언트에는 기본 메시지 전달
        ApiResponse<?> errorResponse = ApiResponse.errorWithOutData(exception, exception.getStatus());
        String json = new ObjectMapper().writeValueAsString(errorResponse);
        response.getWriter().write(json);
    }

    // JWT 토큰에서 추출한 정보로 사용자 인증 정보 설정
    private void setAuthentication(Claims claims) {
        String email = claims.get(JwtTokenProvider.EMAIL_CLAIM, String.class);
        UserRole userRole = UserRole.valueOf(
                claims.get(JwtTokenProvider.USER_ROLE_CLAIM, String.class));
        UUID userId = UUID.fromString(claims.getSubject());

        String role = userRole.name().startsWith("ROLE_") ? userRole.name() : "ROLE_" + userRole.name();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

        // AuthenticatedUser로 인증 정보 설정
        AuthenticatedUser userDetails = new AuthenticatedUser(userId, email, Collections.singletonList(authority));

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 설정된 사용자 정보를 로그 출력
        log.info("생성된 AuthenticatedUser ID: {}, Email: {}, Role: {}", userId, email, role);
    }

}