package com.seolstudy.seolstudy_backend.auth.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.seolstudy.seolstudy_backend.auth.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResponseDto {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
    @JsonProperty("user")
    private MemberDto memberDto;
}