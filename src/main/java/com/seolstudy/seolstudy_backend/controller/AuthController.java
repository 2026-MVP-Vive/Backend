package com.seolstudy.seolstudy_backend.controller;

import com.seolstudy.seolstudy_backend.dto.LoginRequestDto;
import com.seolstudy.seolstudy_backend.dto.TokenResponseDto;
import com.seolstudy.seolstudy_backend.global.common.ApiResponse;
import com.seolstudy.seolstudy_backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponseDto>> login(@RequestBody LoginRequestDto loginRequestDto){
        String token = authService.login(loginRequestDto);
        TokenResponseDto responseData = new TokenResponseDto(token, "Bearer");
        return ResponseEntity.ok(ApiResponse.success(responseData));
    }
}
