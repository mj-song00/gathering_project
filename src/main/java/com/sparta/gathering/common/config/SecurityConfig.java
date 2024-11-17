package com.sparta.gathering.common.config;

import com.sparta.gathering.common.config.jwt.JwtFilter;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final Environment env;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtFilter, SecurityContextHolderAwareRequestFilter.class)
                .authorizeHttpRequests(auth -> {
                    if (Arrays.asList(env.getActiveProfiles()).contains("dev")) {
                        auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll();
                    } else {
                        auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").denyAll();
                    }
                    auth
                            .requestMatchers(
                                    "/**"
                            ).permitAll()
                            .anyRequest().authenticated();
                })
                .formLogin(form -> form
                        .loginPage("/login.html")           // 로그인 페이지 경로
                        .defaultSuccessUrl("/api/auth/login") // 로그인 성공 시 리디렉트
                        .failureUrl("/login.html?error=true") // 로그인 실패 시 리디렉트
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/api/auth/social-login/kakao/success", true) // 소셜 로그인 성공 시 리디렉트
                        .failureUrl("/api/auth/social-login/failure") // 로그인 실패 시 리디렉트
                )
                .build();
    }

    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new CustomHiddenHttpMethodFilter();
    }

}
