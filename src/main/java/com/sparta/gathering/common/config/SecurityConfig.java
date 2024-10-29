package com.sparta.gathering.common.config;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtTokenProvider jwtTokenProvider;
  private final Environment environment;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)  // CSRF 보호 비활성화
        .authorizeHttpRequests(auth -> {
          // dev 프로파일일 경우 Swagger 경로 접근 허용
          if (isDevProfile()) {
            auth.requestMatchers(
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/swagger-ui.html"
            ).permitAll();
          }
          // 인증 없이 접근 가능한 경로 설정
          auth
              .requestMatchers(
                  "/**"                // 모든 경로
                  /* "/",                 // 루트 경로
                  "/api/auth/login",  // 일반 로그인 API
                  "/api/users/signup", // 회원가입 API
                  "/oauth2/**",       // OAuth2 인증 경로
                  "/login/oauth2/code/kakao", // 카카오 Redirect URI
                  "/login.html",           // 로그인 페이지 접근 허용
                  "/signup.html",          // 회원가입 페이지 접근 허용
                  "/js/**",               // JavaScript 파일 접근 허용
                  "/home.html",            // 홈 페이지 접근 허용
                  "/error/**"              // 에러 페이지 접근 허용*/
              ).permitAll()
              .anyRequest().authenticated(); // 그 외의 요청은 인증 필요
        })
        /*.formLogin(form -> form
            .loginPage("/login.html") // 로그인 페이지 경로
            .defaultSuccessUrl("/api/auth/login") // 로그인 성공 시 리디렉트
            .failureUrl("/login.html?error=true") // 로그인 실패 시 리디렉트
        )
        .oauth2Login(oauth2 -> oauth2
            .defaultSuccessUrl("/api/auth/social-login/kakao/success",
                true)   // 소셜 로그인 성공 시 리디렉트
            .failureUrl("/api/auth/social-login/failure")    // 로그인 실패 시 리디렉트
        )*/
        // JWT 인증 필터 추가
        .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  // JWT 인증 필터를 빈으로 등록
  @Bean
  public JwtFilter jwtAuthenticationFilter() {
    return new JwtFilter(jwtTokenProvider);
  }

  // 현재 활성화된 프로파일 dev 여부 확인
  private boolean isDevProfile() {
    return Arrays.asList(environment.getActiveProfiles()).contains("dev");
  }
}