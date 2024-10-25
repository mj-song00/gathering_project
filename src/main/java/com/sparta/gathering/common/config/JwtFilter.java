package com.sparta.gathering.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
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
@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";

  private final JwtTokenProvider jwtTokenProvider;

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain chain) throws IOException {
    String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
    if (bearerToken == null || !bearerToken.startsWith(BEARER_PREFIX)) {
      sendErrorResponse(response, ExceptionEnum.MALFORMED_JWT_TOKEN);
      return;
    }

    try {
      Claims claims = jwtTokenProvider.extractClaims(jwtTokenProvider.substringToken(bearerToken));
      setAuthentication(claims);
      chain.doFilter(request, response);
    } catch (SecurityException e) {
      sendErrorResponse(response, ExceptionEnum.JWT_TOKEN_NOT_FOUND);
    } catch (MalformedJwtException e) {
      sendErrorResponse(response, ExceptionEnum.INVALID_JWT_SIGNATURE);
    } catch (ExpiredJwtException e) {
      sendErrorResponse(response, ExceptionEnum.EXPIRED_JWT_TOKEN);
    } catch (UnsupportedJwtException e) {
      sendErrorResponse(response, ExceptionEnum.UNSUPPORTED_JWT_TOKEN);
    } catch (IllegalArgumentException e) {
      sendErrorResponse(response, ExceptionEnum.INVALID_JWT_TOKEN);
    } catch (Exception e) {
      sendErrorResponse(response, ExceptionEnum.INTERNAL_SERVER_ERROR);
    }
  }

  private void sendErrorResponse(HttpServletResponse response, ExceptionEnum exception)
      throws IOException {

    response.setStatus(exception.getStatus().value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());

    ApiResponse<?> errorResponse = ApiResponse.errorWithOutData(exception, exception.getStatus());

    String json = new ObjectMapper().writeValueAsString(errorResponse);

    response.getWriter().write(json);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    return path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") || path.startsWith(
        "/api/users/signup") || path.startsWith("/api/auth/login");
  }

  private void setAuthentication(Claims claims) {
    String email = claims.get(JwtTokenProvider.EMAIL_CLAIM, String.class);
    UserRole userRole = UserRole.valueOf(
        claims.get(JwtTokenProvider.USER_ROLE_CLAIM, String.class));

    // subject에서 UUID 변환
    UUID userId = UUID.fromString(claims.getSubject());

    // User 객체 생성
    User user = User.createWithMinimumInfo(userId, email, userRole);

    String role = userRole.name().startsWith("ROLE_") ? userRole.name() : "ROLE_" + userRole.name();

    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(user, null,
            Collections.singletonList(new SimpleGrantedAuthority(role)));
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

}