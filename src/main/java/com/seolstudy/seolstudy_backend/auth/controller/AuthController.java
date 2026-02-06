package com.seolstudy.seolstudy_backend.auth.controller;

import com.seolstudy.seolstudy_backend.auth.dto.LoginRequestDto;
import com.seolstudy.seolstudy_backend.auth.dto.RefreshRequest;
import com.seolstudy.seolstudy_backend.auth.dto.RefreshTokenResponse;
import com.seolstudy.seolstudy_backend.auth.dto.TokenResponseDto;
import com.seolstudy.seolstudy_backend.global.common.ApiResponse;
import com.seolstudy.seolstudy_backend.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponseDto>> login(@RequestBody LoginRequestDto loginRequestDto){
        TokenResponseDto responseDto = authService.login(loginRequestDto);
        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshToken(@RequestBody RefreshRequest request){
        RefreshTokenResponse tokenResponse = authService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(tokenResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(){
        authService.logout();
        return ResponseEntity.ok(ApiResponse.success("로그아웃 되었습니다."));
    }
}
