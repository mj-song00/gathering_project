package com.sparta.gathering.domain.user.repository;

import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.enums.IdentityProvider;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByProviderIdAndIdentityProvider(String providerId,
            IdentityProvider identityProvider);

}
