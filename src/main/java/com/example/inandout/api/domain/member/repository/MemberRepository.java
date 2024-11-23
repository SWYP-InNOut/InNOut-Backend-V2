package com.example.inandout.api.domain.member.repository;

import com.example.inandout.api.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
