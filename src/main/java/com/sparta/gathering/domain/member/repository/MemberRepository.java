package com.sparta.gathering.domain.member.repository;

import com.sparta.gathering.domain.member.entity.Member;
import com.sparta.gathering.domain.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    void findByUserId(User user);
    List<Member> findByGatherId(Pageable pageable, long gatherId);

    @Query("SELECT m.user.id FROM Member m WHERE m.gather.id = :gatherId AND m.permission = 'OWNER'")
    Optional<UUID> findOwnerIdByGatherId(@Param("gatherId") Long gatherId);
}
