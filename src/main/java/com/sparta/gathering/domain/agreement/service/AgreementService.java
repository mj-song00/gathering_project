package com.sparta.gathering.domain.agreement.service;

import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.agreement.dto.request.AgreementRequestDto;
import com.sparta.gathering.domain.agreement.dto.request.AgreementUpdateRequestDto;
import com.sparta.gathering.domain.agreement.entity.Agreement;
import com.sparta.gathering.domain.agreement.repository.AgreementRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AgreementService {

    private final AgreementRepository agreementRepository;

    @Transactional
    public void createAgreement(AgreementRequestDto agreementRequestDto) {
        Agreement agreement = new Agreement(agreementRequestDto.getContent(), agreementRequestDto.getVersion());
        agreementRepository.save(agreement);
    }

    public Agreement getLatestAgreementOrThrow() {
        return agreementRepository.findLatestAgreement()
                .orElseThrow(() -> new BaseException(ExceptionEnum.AGREEMENT_NOT_FOUND));
    }

    @Transactional
    public Agreement updateAgreement(UUID id, AgreementUpdateRequestDto agreementUpdateRequestDto) {
        Agreement agreement = agreementRepository.findById(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.AGREEMENT_NOT_FOUND));

        agreement.updateContent(agreementUpdateRequestDto.getContent());
        return agreement;
    }
}

