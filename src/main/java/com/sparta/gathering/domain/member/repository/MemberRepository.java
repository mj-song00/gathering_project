package com.sparta.gathering.domain.member.repository;

import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.member.entity.Member;
import com.sparta.gathering.domain.member.enums.Permission;
import com.sparta.gathering.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Page<Member> findByGatherIdAndDeletedAtIsNull(Pageable pageable, long gatherId);

    @Query("SELECT m.user.id, m.permission FROM Member m WHERE m.gather.id = :gatherId and m.permission = com.sparta.gathering.domain.member.enums.Permission.MANAGER")
    Optional<UUID> findManagerIdByGatherId(@Param("gatherId") Long gatherId);

    Optional<Member> findByUserId(UUID id);

    Optional<Member> findByGatherIdAndPermission(long gatherId, Permission permission);

    @Query("SELECT m.permission FROM Member m WHERE m.user.id = :userId")
    Permission findPermissionByUserId(@Param("userId") UUID userId);

    Optional<Member> findByUserAndGather(User user, Gather gather);

    Optional<Member> findByIdAndGatherId(Long memberId, Long gatherId);
}
