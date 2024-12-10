package com.example.inandout.global.auth.domain;

import com.example.inandout.api.domain.member.entity.Member;
import com.example.inandout.api.domain.member.value.MemberStatus;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
public class PrincipalDetails implements UserDetails {
    private final Member member;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return member.getStatus().equals(MemberStatus.ACTIVE);
    }

    public Long getMemberId() {
        return member.getId();
    }
}
