package com.example.inandout.api.domain.member.repository;

import com.example.inandout.api.domain.member.entity.Member;
import com.example.inandout.api.domain.member.value.LoginType;
import com.example.inandout.api.domain.member.value.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByStatusAndId(MemberStatus status, Long id);
    Optional<Member> findByLoginTypeAndEmail(LoginType loginType, String email);
    Optional<Member> findByAuthToken(String authToken);
    boolean existsByStatusAndId(MemberStatus status, Long id);
    boolean existsByName(String name);
}
