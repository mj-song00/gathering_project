package com.sparta.gathering.domain.agreement.service;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.common.service.BatchJobRequestService;
import com.sparta.gathering.domain.agreement.dto.request.AgreementRequestDto;
import com.sparta.gathering.domain.agreement.dto.request.AgreementUpdateRequestDto;
import com.sparta.gathering.domain.agreement.entity.Agreement;
import com.sparta.gathering.domain.agreement.entity.AgreementHistory;
import com.sparta.gathering.domain.agreement.enums.AgreementType;
import com.sparta.gathering.domain.agreement.repository.AgreementHistoryRepository;
import com.sparta.gathering.domain.agreement.repository.AgreementRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgreementService {

    private final AgreementRepository agreementRepository;
    private final AgreementHistoryRepository agreementHistoryRepository;
    private final BatchJobRequestService batchJobRequestService;

    @Transactional
    public void createAgreement(AgreementRequestDto agreementRequestDto, AuthenticatedUser authenticatedUser) {

        // 인증되지 않은 사용자는 접근 불가
        if (authenticatedUser == null) {
            throw new BaseException(ExceptionEnum.UNAUTHORIZED_USER);
        }

        // ROULE_ADMIN 권한이 없는 사용자는 접근 불가
        if (authenticatedUser.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            throw new BaseException(ExceptionEnum.UNAUTHORIZED_ACTION);
        }

        // 중복된 약관 버전 확인
        boolean exists = agreementRepository.existsByTypeAndVersion(
                agreementRequestDto.getType(),
                agreementRequestDto.getVersion()
        );
        if (exists) {
            throw new BaseException(ExceptionEnum.DUPLICATE_AGREEMENT_VERSION);
        }

        // 약관 생성
        Agreement agreement = new Agreement(
                UUID.randomUUID(),
                agreementRequestDto.getContent(),
                agreementRequestDto.getVersion(),
                agreementRequestDto.getType()
        );
        agreementRepository.save(agreement);
    }

    // 최신 약관 조회 (특정 타입)
    @Transactional(readOnly = true)
    public Agreement getLatestAgreement(AgreementType type) {
        return agreementRepository.findLatestAgreementByType(type, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElseThrow(() -> new BaseException(ExceptionEnum.AGREEMENT_NOT_FOUND));
    }

    // 모든 최신 약관 조회
    @Transactional(readOnly = true)
    public List<Agreement> getAllLatestAgreements() {
        return agreementRepository.findAllLatestAgreements();
    }

    // 약관 ID로 조회
    @Transactional(readOnly = true)
    public Agreement getAgreementById(UUID agreementId) {
        return agreementRepository.findById(agreementId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.AGREEMENT_NOT_FOUND));
    }

    // 약관 업데이트
    @Transactional
    public void updateAgreement(
            UUID agreementId,
            AgreementUpdateRequestDto agreementUpdateRequestDto,
            AuthenticatedUser authenticatedUser) {

        // 인증 및 권한 확인
        if (authenticatedUser == null) {
            throw new BaseException(ExceptionEnum.UNAUTHORIZED_USER);
        }

        if (authenticatedUser.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            throw new BaseException(ExceptionEnum.UNAUTHORIZED_ACTION);
        }

        // 기존 약관 조회
        Agreement agreement = agreementRepository.findById(agreementId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.AGREEMENT_NOT_FOUND));

        // 변경 사항 검증
        if (agreement.getContent().equals(agreementUpdateRequestDto.getContent()) &&
                agreement.getVersion().equals(agreementUpdateRequestDto.getVersion())) {
            throw new BaseException(ExceptionEnum.SAME_AGREEMENT);
        }

        // 기존 약관을 AgreementHistory에 저장
        AgreementHistory history = new AgreementHistory(
                agreement.getId(),
                agreement.getContent(),
                agreement.getVersion(),
                agreement.getType()
        );
        agreementHistoryRepository.save(history);

        // 약관 정보 업데이트
        agreement.updateContentAndVersion(
                agreementUpdateRequestDto.getContent(),
                agreementUpdateRequestDto.getVersion()
        );
        agreementRepository.save(agreement);

        // 배치 작업 실행 요청 추가 (대기열에 저장)
        batchJobRequestService.createBatchJobRequest("userAgreementPendingReagreeJob", agreement.getId().toString());
    }

}

