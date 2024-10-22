package com.sparta.gathering.domain.user.service;

import com.sparta.gathering.domain.user.dto.request.UserRequest;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.enums.IdentityProvider;
import com.sparta.gathering.domain.user.enums.UserRole;
import com.sparta.gathering.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(UserRequest userRequest) {

        User user = new User(
                userRequest.getEmail(),
                userRequest.getNickName(),
                userRequest.getPassword(),
                UserRole.ROLE_USER,  // 기본적으로 ROLE_USER로 설정
                userRequest.getIdentityProvider()  // 일반 로그인 사용자는 NONE
        );
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setDelete(true);  // Soft delete 처리
        userRepository.save(user);  // 변경된 사용자 저장
    }

    // ↓ 임시
    @Override
    public User findById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User findByProviderIdAndIdentityProvider(String providerId, IdentityProvider identityProvider) {
        return userRepository.findByProviderIdAndIdentityProvider(providerId, identityProvider)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

}

