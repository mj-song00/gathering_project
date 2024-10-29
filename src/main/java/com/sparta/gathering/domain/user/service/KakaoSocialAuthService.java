package com.sparta.gathering.domain.user.service;

import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.enums.IdentityProvider;
import com.sparta.gathering.domain.user.enums.UserRole;
import com.sparta.gathering.domain.user.repository.UserRepository;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KakaoSocialAuthService {

    private final UserRepository userRepository;

    @Transactional
    public User processOauth2User(OAuth2User oauth2user) {
        Object providerIdObj = oauth2user.getAttribute("id");
        String providerId = providerIdObj != null ? String.valueOf(providerIdObj) : null;

        Map<String, Object> kakaoAccount = oauth2user.getAttribute("kakao_account");
        if (kakaoAccount == null) {
            throw new BaseException(ExceptionEnum.KAKAO_DATA_PROCESSING_ERROR);
        }

        String email = (String) kakaoAccount.get("email");
        if (email == null) {
            throw new BaseException(ExceptionEnum.EMAIL_NOT_FOUND);
        }

        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        String nickName = profile != null ? (String) profile.get("nickname") : null;
        String profileImage = profile != null ? (String) profile.get("profile_image_url") : null;

        // 동일 이메일로 기존 유저 검색
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            // 기존 유저 정보 업데이트
            user.updateProviderInfo(providerId, IdentityProvider.KAKAO, nickName, profileImage);
            return user;
        }

        // 신규 유저 생성
        User newUser = User.createSocialUser(email, nickName, UserRole.ROLE_USER,
                IdentityProvider.KAKAO, providerId, profileImage);
        return userRepository.save(newUser);
    }

}
