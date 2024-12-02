package com.sparta.gathering.domain.user.dto.request;

import com.sparta.gathering.domain.user.enums.IdentityProvider;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickName;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$&*]).{8,20}$", message = "비밀번호는 8자 이상 20자 이하이며, 영문 대문자와 특수문자를 포함해야 합니다.")
    private String password;

    @NotNull(message = "로그인 제공자가 올바르지 않습니다.")
    private IdentityProvider identityProvider;

    @NotEmpty(message = "필수 약관에 동의해야 합니다.")
    private List<UUID> agreedAgreementIds; // 동의한 약관 ID 목록

    // 특정 약관 ID에 동의 여부를 확인하는 메서드
    public boolean hasAgreedTo(UUID agreementId) {
        return agreedAgreementIds.contains(agreementId);
    }

}