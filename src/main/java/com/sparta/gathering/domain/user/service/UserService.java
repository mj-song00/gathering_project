package com.sparta.gathering.domain.user.service;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.domain.user.dto.request.SignupRequest;
import com.sparta.gathering.domain.user.entity.User;

public interface UserService {

    User createUser(SignupRequest userRequest);

    void deleteUser(AuthenticatedUser authenticatedUser);

    User findByEmail(String email);

    User authenticateUser(String email, String password);

}
