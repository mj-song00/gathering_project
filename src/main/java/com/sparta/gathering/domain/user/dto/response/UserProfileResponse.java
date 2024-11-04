package com.sparta.gathering.domain.user.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserProfileResponse {

    private UUID id;
    private String email;
    private String nickName;
    private String profileImage;
}
