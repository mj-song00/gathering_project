package com.sparta.gathering.domain.user.service;

import com.sparta.gathering.domain.user.entity.User;

public interface RefreshTokenService {

    String createRefreshToken(User user);

    boolean validateRefreshToken(String refreshToken);

    User findUserByRefreshToken(String refreshToken);

}
