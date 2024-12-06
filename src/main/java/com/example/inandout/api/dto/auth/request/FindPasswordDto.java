package com.example.inandout.api.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class FindPasswordDto {
    @NotBlank
    private String email;
}
