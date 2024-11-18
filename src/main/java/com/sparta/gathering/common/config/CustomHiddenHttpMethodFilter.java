package com.sparta.gathering.common.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.filter.HiddenHttpMethodFilter;

public class CustomHiddenHttpMethodFilter extends HiddenHttpMethodFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return !requestURI.startsWith("/api/user-agreements/reagree");
    }

}
