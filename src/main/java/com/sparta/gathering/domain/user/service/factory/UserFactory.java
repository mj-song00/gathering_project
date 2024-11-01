package com.sparta.gathering.domain.user.service.factory;

import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.enums.IdentityProvider;
import com.sparta.gathering.domain.user.enums.UserRole;
import java.util.UUID;

public class UserFactory {

    // 인스턴스화 시도 + 리플렉션 방지 (ex.new UserFactory() 같은 코드 사용 시)
    // 클래스가 설계도라면 인스턴스화는 설계도를 바탕으로 실제 물건을 만드는 것
    // 설계도는 여러 개의 물건을 만들 수 있지만, 설계도 자체는 물건(실체?)이 될 수 없음
    // 즉, 설계도를 바탕으로 물건을 만드는 것은 가능하지만, 설계도 자체를 물건으로 만드는 것은 불가능
    // UserFactory 클래스는 정적 메서드만 제공(정해진 설계도룰 제공)하므로 인스턴스화(새로운 설계도로 물건을 만들 필요)가 필요 없음
    // 불필요한 생성자를 막아 불필요한 메모리 사용을 줄이고 의도된 생성 방식만 사용하도록 private 생성자를 선언함
    // 리플렉션 = 자바 클래스의 모든 구조(필드, 메서드, private 생성자 등)에 접근 및 조작할 수 있는 기능
    private UserFactory() {
        throw new BaseException(ExceptionEnum.NOT_INSTANTIABLE_CLASS); // 리플렉션을 통한 인스턴스화 방지
    }

    public static User of(String email, String nickName, String password,
            UserRole userRole, IdentityProvider identityProvider, String profileImage) {
        return User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .nickName(nickName)
                .password(password)
                .userRole(userRole)
                .identityProvider(identityProvider)
                .profileImage(profileImage)
                .build();
    }

    public static User ofSocial(String email, String nickName, UserRole userRole,
            IdentityProvider identityProvider, String providerId, String profileImage) {
        return User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .nickName(nickName)
                .userRole(userRole)
                .identityProvider(identityProvider)
                .providerId(providerId)
                .profileImage(profileImage)
                .build();
    }

}
