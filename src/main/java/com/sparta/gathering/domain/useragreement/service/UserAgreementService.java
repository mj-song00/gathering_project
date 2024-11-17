package com.sparta.gathering.domain.useragreement.service;

import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.agreement.entity.Agreement;
import com.sparta.gathering.domain.agreement.enums.AgreementStatus;
import com.sparta.gathering.domain.agreement.repository.AgreementRepository;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.repository.UserRepository;
import com.sparta.gathering.domain.useragreement.dto.response.UserAgreementResponseDto;
import com.sparta.gathering.domain.useragreement.entity.UserAgreement;
import com.sparta.gathering.domain.useragreement.entity.UserAgreementHistory;
import com.sparta.gathering.domain.useragreement.repository.UserAgreementHistoryRepository;
import com.sparta.gathering.domain.useragreement.repository.UserAgreementRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAgreementService {

    private final UserAgreementRepository userAgreementRepository;
    private final UserAgreementHistoryRepository userAgreementHistoryRepository;
    private final UserRepository userRepository;
    private final AgreementRepository agreementRepository;
    private final UserAgreementNotificationService notificationService;

    // 사용자의 동의 상태 조회
    @Transactional(readOnly = true)
    public UserAgreementResponseDto getUserAgreementStatus(UUID userId, UUID agreementId) {
        UserAgreement userAgreement = userAgreementRepository.findByUserIdAndAgreementId(userId, agreementId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_AGREEMENT_NOT_FOUND));

        return new UserAgreementResponseDto(
                userAgreement.getUser().getId(),
                userAgreement.getAgreement().getId(),
                userAgreement.getStatus(),
                userAgreement.getAgreedAt()
        );
    }

    // 약관 재동의
    @Transactional
    public void reAgreeToTerms(UUID userId, UUID agreementId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));

        Agreement agreement = agreementRepository.findById(agreementId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.AGREEMENT_NOT_FOUND));

        UserAgreement userAgreement = userAgreementRepository.findByUserAndAgreement(user, agreement)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_AGREEMENT_NOT_FOUND));

        if (userAgreement.getStatus() == AgreementStatus.AGREED) {
            throw new BaseException(ExceptionEnum.ALREADY_AGREED);
        }

        saveUserAgreementHistory(userAgreement);

        userAgreement.updateStatus(AgreementStatus.AGREED);
        userAgreementRepository.save(userAgreement);
    }

    // 이전 상태를 UserAgreementHistory에 기록
    public void saveUserAgreementHistory(UserAgreement userAgreement) {
        UserAgreementHistory history = new UserAgreementHistory(
                userAgreement.getUser().getId(),
                userAgreement.getAgreement().getId(),
                userAgreement.getStatus(),
                userAgreement.getAgreedAt()
        );
        userAgreementHistoryRepository.save(history);
    }

    @Transactional(readOnly = true)
    public List<UserAgreementResponseDto> getUserAgreementsByStatus(AgreementStatus status) {
        return userAgreementRepository.findByStatus(status).stream()
                .map(agreement -> new UserAgreementResponseDto(
                        agreement.getUser().getId(),
                        agreement.getAgreement().getId(),
                        agreement.getStatus(),
                        agreement.getAgreedAt()
                ))
                .collect(Collectors.toList());
    }

}
