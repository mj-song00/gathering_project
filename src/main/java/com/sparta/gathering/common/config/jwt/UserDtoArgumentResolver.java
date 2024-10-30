package com.sparta.gathering.common.config.jwt;

import com.sparta.gathering.domain.user.dto.response.UserDTO;
import com.sparta.gathering.domain.user.enums.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
public class UserDtoArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(UserDTO.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser authUser) {
            log.info("User ID: {}", authUser.getUserId());
            log.info("User Email: {}", authUser.getEmail());
            authUser.getAuthorities().forEach(authority -> log.info("Authority: {}", authority.getAuthority()));
            return new UserDTO(authUser.getUserId(), authUser.getEmail(),
                    authUser.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .map(UserRole::valueOf)
                            .findFirst()
                            .orElse(null));
        }
        return null;
    }
}
