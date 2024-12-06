package com.example.inandout.api.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class MemberNameDto {
    @NotBlank
    private String name;
}
