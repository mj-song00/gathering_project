package com.sparta.gathering.domain.agreement.service;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
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
    public void createAgreement(AgreementRequestDto agreementRequestDto, AuthenticatedUser authenticatedUser) {

        // 인증되지 않은 사용자는 접근 불가
        if (authenticatedUser == null) {
            throw new BaseException(ExceptionEnum.UNAUTHORIZED_USER);
        }

        // ROULE_ADMIN 권한이 없는 사용자는 접근 불가  Collection<? extends GrantedAuthority> authorities; // 사용자 권한 정보
        if (authenticatedUser.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            throw new BaseException(ExceptionEnum.UNAUTHORIZED_ACTION);
        }

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

