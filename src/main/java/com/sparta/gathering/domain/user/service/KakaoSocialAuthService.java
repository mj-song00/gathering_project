package com.sparta.gathering.domain.user.service;

import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.enums.IdentityProvider;
import com.sparta.gathering.domain.user.enums.KakaoAttributes;
import com.sparta.gathering.domain.user.enums.UserRole;
import com.sparta.gathering.domain.user.repository.UserRepository;
import com.sparta.gathering.domain.user.service.factory.UserFactory;
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

    // 카카오 소셜 로그인 사용자 처리
    @Transactional
    public User processOauth2User(OAuth2User oauth2user) {
        String providerId = getProviderId(oauth2user);       // 카카오에서 제공하는 고유 ID
        String email = getEmail(oauth2user);                 // 카카오에서 제공하는 이메일
        String nickName = getNickname(oauth2user);           // 카카오에서 제공하는 닉네임
        String profileImage = getProfileImage(oauth2user);   // 카카오에서 제공하는 프로필 이미지 URL

        // 이메일로 유저를 찾고 없으면 생성, 있으면 기존 유저 반환
        return userRepository.findByEmail(email)
                .orElseGet(() -> createUser(email, providerId, nickName, profileImage));
    }

    // 카카오 고유 ID 추출
    private String getProviderId(OAuth2User oauth2user) {
        return Optional.ofNullable(oauth2user.getAttribute("id"))
                .map(String::valueOf)
                .orElseThrow(() -> new BaseException(ExceptionEnum.KAKAO_DATA_PROCESSING_ERROR));
    }

    // 카카오 이메일 추출
    private String getEmail(OAuth2User oauth2user) {
        Map<String, Object> kakaoAccount = getKakaoAccount(oauth2user);
        return Optional.ofNullable((String) kakaoAccount.get(KakaoAttributes.EMAIL.getAttribute()))
                .orElseThrow(() -> new BaseException(ExceptionEnum.EMAIL_NOT_FOUND));
    }

    // 카카오 닉네임 추출 (닉네임이 없을 경우 디폴트 닉네임 반환)
    private String getNickname(OAuth2User oauth2user) {
        Map<String, Object> profile = getProfile(oauth2user);
        return profile != null ? (String) profile.get(KakaoAttributes.NICKNAME.getAttribute())
                : KakaoAttributes.DEFAULT_NICKNAME.getAttribute();
    }

    // 카카오 프로필 이미지 URL 추출 (프로필 이미지가 없을 경우 null 반환, 향후 디폴트 이미지로 대체 필요)
    private String getProfileImage(OAuth2User oauth2user) {
        Map<String, Object> profile = getProfile(oauth2user);
        return profile != null ? (String) profile.get(
                KakaoAttributes.PROFILE_IMAGE_URL.getAttribute()) : null;
    }

    // 카카오 계정 정보 추출
    @SuppressWarnings("unchecked") // 형 변환 전 isInstance 타입 확인 후 안전하게 캐스팅, 경고 억제를 위한 어노테이션
    private Map<String, Object> getKakaoAccount(OAuth2User oauth2user) {
        return Optional.ofNullable(
                        oauth2user.getAttribute(KakaoAttributes.KAKAO_ACCOUNT.getAttribute()))
                .filter(Map.class::isInstance)
                .map(attr -> (Map<String, Object>) attr)
                .orElseThrow(() -> new BaseException(
                        ExceptionEnum.KAKAO_DATA_PROCESSING_ERROR)); // 카카오 계정 정보가 없을 경우 예외 발생
    }

    // 카카오 프로필 정보 추출
    @SuppressWarnings("unchecked") // 형 변환 전 isInstance 타입 확인 후 안전하게 캐스팅, 경고 억제를 위한 어노테이션
    private Map<String, Object> getProfile(OAuth2User oauth2user) {
        Map<String, Object> kakaoAccount = getKakaoAccount(oauth2user);
        return (Map<String, Object>) Optional.ofNullable(
                        kakaoAccount.get(KakaoAttributes.PROFILE.getAttribute()))
                .filter(Map.class::isInstance)
                .orElseThrow(() -> new BaseException(
                        ExceptionEnum.KAKAO_DATA_PROCESSING_ERROR)); // 프로필 정보가 없을 경우 예외 발생
    }

    // 카카오 소셜 로그인 사용자 생성
    private User createUser(String email, String providerId, String nickName, String profileImage) {
        User user = UserFactory.ofSocial(
                email,
                nickName,
                UserRole.ROLE_USER,
                IdentityProvider.KAKAO,
                providerId,
                profileImage
        );
        return userRepository.save(user);
    }

}
