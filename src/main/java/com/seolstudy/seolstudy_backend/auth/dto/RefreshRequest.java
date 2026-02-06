package com.seolstudy.seolstudy_backend.auth.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefreshRequest {
    private String refreshToken;
}
