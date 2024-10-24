package com.sparta.gathering.domain.user.service;

import com.sparta.gathering.domain.user.entity.RefreshToken;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public String createRefreshToken(User user) {
        String token = UUID.randomUUID().toString();
        Timestamp expiryDate = new Timestamp(new Date().getTime() + 3600000); // 1시간 만료
        RefreshToken refreshToken = new RefreshToken(user, token, expiryDate);
        refreshTokenRepository.save(refreshToken);
        return token;
    }

    @Override
    public boolean validateRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .map(token -> token.getExpiryDate().after(new Timestamp(new Date().getTime())))
                .orElse(false);
    }

    @Override
    public User findUserByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .map(RefreshToken::getUser)
                .orElseThrow(() -> new RuntimeException("리프레시 토큰이 유효하지 않습니다."));
    }

}

