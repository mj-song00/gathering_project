package com.sparta.gathering.common.config.batch;

import com.sparta.gathering.domain.agreement.enums.AgreementStatus;
import com.sparta.gathering.domain.useragreement.entity.UserAgreement;
import com.sparta.gathering.domain.useragreement.repository.UserAgreementRepository;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;

@RequiredArgsConstructor
public class UserAgreementReader implements ItemReader<UserAgreement> {

    private final UserAgreementRepository userAgreementRepository;
    private final String agreementIdStr;
    private Iterator<UserAgreement> iterator;

    @Override
    public UserAgreement read() {
        if (iterator == null) {
            UUID agreementId = UUID.fromString(agreementIdStr);
            List<UserAgreement> userAgreements = userAgreementRepository.findByAgreementIdAndStatus(
                    agreementId, AgreementStatus.AGREED);
            iterator = userAgreements.iterator();
        }
        return iterator.hasNext() ? iterator.next() : null;
    }
}
