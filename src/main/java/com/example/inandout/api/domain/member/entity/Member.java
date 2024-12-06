package com.example.inandout.api.domain.member.entity;

import com.example.inandout.api.domain.member.value.MemberStatus;
import com.example.inandout.api.domain.member.value.LoginType;
import com.example.inandout.api.dto.auth.request.JoinRequestDto;
import com.example.inandout.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Email
    @Column(length = 100, nullable = false)
    private String email;

    @Column(length = 500, nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    private String authToken;

    @Column(nullable = false)
    @ColumnDefault("'NONCERTIFIED'")
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @Column(nullable = false)
    @ColumnDefault("true")
    private Boolean isPublic;

    @Column(nullable = false)
    @ColumnDefault("1")
    private int memberImageId;

    public Member(Long id,
                  String name,
                  String email,
                  String password,
                  LoginType loginType,
                  String authToken,
                  MemberStatus status,
                  Boolean isPublic,
                  int memberImageId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.loginType = loginType;
        this.authToken = authToken;
        this.status = status;
        this.isPublic = isPublic;
        this.memberImageId = memberImageId;
    }

    public static Member createGeneralMember(String name, String email, String password, int memberImageId, String authToken) {
        return Member.builder()
                .name(name)
                .email(email)
                .password(password)
                .loginType(LoginType.GENERAL)
                .authToken(authToken)
                .status(MemberStatus.NONCERTIFIED)
                .isPublic(false)
                .memberImageId(memberImageId)
                .build();
    }

    public void updateToken(String authToken) {
        this.authToken = authToken;
    }

    public void updateStatus(MemberStatus memberStatus) {
        this.status = memberStatus;
    }
}
