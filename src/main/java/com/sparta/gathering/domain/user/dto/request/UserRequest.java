package com.sparta.gathering.domain.user.dto.request;

import com.sparta.gathering.domain.user.enums.IdentityProvider;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private String email;
    private String nickName;
    private String password;
    private IdentityProvider identityProvider; // NONE, GOOGLE, KAKAO 중 선택
}