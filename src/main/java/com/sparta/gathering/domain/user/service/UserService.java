package com.sparta.gathering.domain.user.service;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.domain.user.dto.request.SignupRequest;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.enums.IdentityProvider;
import java.util.UUID;

public interface UserService {

    User createUser(SignupRequest userRequest);

    User findById(UUID userId);

    void deleteUser(AuthenticatedUser authenticatedUser);

    User findByEmail(String email);

    User findByProviderIdAndIdentityProvider(String providerId, IdentityProvider identityProvider);

    User authenticateUser(String email, String password);

}
