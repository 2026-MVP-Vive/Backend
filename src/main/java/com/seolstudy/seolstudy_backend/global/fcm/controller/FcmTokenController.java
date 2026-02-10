package com.seolstudy.seolstudy_backend.global.fcm.controller;

import com.seolstudy.seolstudy_backend.global.fcm.domain.FcmToken;
import com.seolstudy.seolstudy_backend.global.fcm.repository.FcmTokenRepository;
import com.seolstudy.seolstudy_backend.global.fcm.service.FcmService;
import com.seolstudy.seolstudy_backend.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.rmi.MarshalledObject;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/fcm")
@RequiredArgsConstructor
public class FcmTokenController {
    private final FcmService fcmService;
    private final SecurityUtil securityUtil;

    /**
     * 현재 로그인한 유저의 토큰 갱신 또는 저장을 수행하는 컨트롤러
     * */
    @PostMapping("/token")
    public ResponseEntity<Map<String, Object>> registerToken(@RequestBody Map<String, String> request){
        Long userId = securityUtil.getCurrentUserId();
        String token = request.get("token");

        fcmService.saveOrUpdateToken(userId, token);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }
}
