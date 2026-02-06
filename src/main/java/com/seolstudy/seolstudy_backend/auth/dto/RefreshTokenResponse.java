package com.seolstudy.seolstudy_backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshTokenResponse {
    private String accessToken;
    private long expiresIn;
}
