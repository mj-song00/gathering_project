package com.sparta.gathering.domain.user.service;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.domain.user.dto.request.SignupRequest;
import com.sparta.gathering.domain.user.dto.response.UserProfileResponse;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {

    void createUser(SignupRequest userRequest);

    UserProfileResponse getUserProfile(AuthenticatedUser authenticatedUser);

    void changePassword(AuthenticatedUser authenticatedUser, String oldPassword, String newPassword);

    void changeNickName(AuthenticatedUser authenticatedUser, String newNickName);

    void deleteUser(AuthenticatedUser authenticatedUser, String refreshToken, HttpServletResponse response);

}
