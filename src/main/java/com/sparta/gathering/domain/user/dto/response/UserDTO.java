package com.sparta.gathering.domain.user.dto.response;

import com.sparta.gathering.domain.user.enums.UserRole;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDTO {

    private UUID userId;
    private String email;
    private UserRole userRole;

}
