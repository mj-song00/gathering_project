package com.sparta.gathering.domain.user.dto.response;

import com.sparta.gathering.domain.user.entity.User;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String email;
    private String nickName;
    private String profileImage;

    public UserResponse(User user) {
        this.email = user.getEmail();
        this.nickName = user.getNickName();
        this.profileImage = user.getProfileImage();
    }
}
