package com.sparta.gathering.domain.useragreement.service;

import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.agreement.entity.Agreement;
import com.sparta.gathering.domain.agreement.enums.AgreementStatus;
import com.sparta.gathering.domain.agreement.repository.AgreementRepository;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.repository.UserRepository;
import com.sparta.gathering.domain.useragreement.dto.request.UserAgreementRequestDto;
import com.sparta.gathering.domain.useragreement.dto.response.UserAgreementResponseDto;
import com.sparta.gathering.domain.useragreement.entity.UserAgreement;
import com.sparta.gathering.domain.useragreement.repository.UserAgreementRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAgreementService {

    private final UserAgreementRepository userAgreementRepository;
    private final UserRepository userRepository;
    private final AgreementRepository agreementRepository;

    @Transactional(readOnly = true)
    public UserAgreementResponseDto getUserAgreementStatus(UUID userId, UUID agreementId) {
        UserAgreement userAgreement = userAgreementRepository.findByUserIdAndAgreementId(userId, agreementId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_AGREEMENT_NOT_FOUND));
        return new UserAgreementResponseDto(userAgreement.getUser().getId(), userAgreement.getAgreement().getId(),
                userAgreement.getStatus(), userAgreement.getAgreedAt());
    }

    @Transactional
    public void agreeToTerms(UserAgreementRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));
        Agreement agreement = agreementRepository.findById(requestDto.getAgreementId())
                .orElseThrow(() -> new BaseException(ExceptionEnum.AGREEMENT_NOT_FOUND));

        UserAgreement userAgreement = new UserAgreement(user, agreement, AgreementStatus.AGREED);
        userAgreementRepository.save(userAgreement);
    }

    @Transactional
    public void reAgreeToTerms(UUID userId, UUID agreementId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));
        Agreement agreement = agreementRepository.findById(agreementId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.AGREEMENT_NOT_FOUND));

        UserAgreement userAgreement = userAgreementRepository.findByUserAndAgreement(user, agreement)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_AGREEMENT_NOT_FOUND));

        userAgreement.updateStatus(AgreementStatus.AGREED);
        userAgreementRepository.save(userAgreement);
    }

}
