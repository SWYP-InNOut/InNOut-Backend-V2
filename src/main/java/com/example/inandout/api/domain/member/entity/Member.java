package com.example.inandout.api.domain.member.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Integer id;

    @Column(length = 100, nullable = false)
    private String name;

    @Email
    @Column(length = 100, nullable = false)
    private String email;

    @Column(length = 500, nullable = false)
    private String password;

    @Builder
    public Member(String name) {
        this.name = name;
    }
}
