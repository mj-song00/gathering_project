package com.sparta.gathering.domain.agreement.service;

import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.agreement.dto.request.AgreementRequestDto;
import com.sparta.gathering.domain.agreement.entity.Agreement;
import com.sparta.gathering.domain.agreement.enums.AgreementType;
import com.sparta.gathering.domain.agreement.repository.AgreementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AgreementService {

    private final AgreementRepository agreementRepository;

    @Transactional
    public void createAgreement(AgreementRequestDto agreementRequestDto) {

        boolean exists = agreementRepository.existsByTypeAndVersion(
                agreementRequestDto.getType(),
                agreementRequestDto.getVersion()
        );
        if (exists) {
            throw new BaseException(ExceptionEnum.DUPLICATE_AGREEMENT_VERSION);
        }

        Agreement agreement = new Agreement(
                agreementRequestDto.getContent(),
                agreementRequestDto.getVersion(),
                agreementRequestDto.getType()
        );
        agreementRepository.save(agreement);
    }

    public Agreement getLatestAgreement(AgreementType type) {
        return agreementRepository.findLatestAgreementByType(type, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElseThrow(() -> new BaseException(ExceptionEnum.AGREEMENT_NOT_FOUND));
    }

}

